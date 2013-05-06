package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.PromotionLevelAlreadyExistException;
import net.ontrack.backend.dao.PromotionLevelDao;
import net.ontrack.backend.dao.model.TPromotionLevel;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class PromotionLevelJdbcDao extends AbstractJdbcDao implements PromotionLevelDao {

    protected final RowMapper<TPromotionLevel> promotionLevelMapper = new RowMapper<TPromotionLevel>() {
        @Override
        public TPromotionLevel mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TPromotionLevel(
                    rs.getInt("id"),
                    rs.getInt("branch"),
                    rs.getInt("levelNb"),
                    rs.getString("name"),
                    rs.getString("description"),
                    rs.getBoolean("autoPromote"));
        }
    };

    @Autowired
    public PromotionLevelJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TPromotionLevel> findByBranch(int branch) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROMOTION_LEVEL_LIST,
                params("branch", branch),
                promotionLevelMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TPromotionLevel getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROMOTION_LEVEL,
                params("id", id),
                promotionLevelMapper);
    }

    @Override
    @Transactional
    public int createPromotionLevel(int branch, String name, String description) {
        // Count of existing promotion levels
        int count = getNamedParameterJdbcTemplate().queryForInt(
                SQL.PROMOTION_LEVEL_COUNT,
                params("branch", branch)
        );
        int levelNb = count + 1;
        // OK
        try {
            return dbCreate(
                    SQL.PROMOTION_LEVEL_CREATE,
                    params("branch", branch)
                            .addValue("name", name)
                            .addValue("description", description)
                            .addValue("levelNb", levelNb));
        } catch (DuplicateKeyException ex) {
            throw new PromotionLevelAlreadyExistException(name);
        }
    }

    @Override
    @Transactional
    public Ack updatePromotionLevel(int promotionLevelId, String name, String description) {
        try {
            return Ack.one(
                    getNamedParameterJdbcTemplate().update(
                            SQL.PROMOTION_LEVEL_UPDATE,
                            params("id", promotionLevelId).addValue("name", name).addValue("description", description)
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new PromotionLevelAlreadyExistException(name);
        }
    }

    @Override
    @Transactional
    public Ack deletePromotionLevel(int promotionLevelId) {
        // Previous level
        TPromotionLevel promotionLevel = getById(promotionLevelId);
        // Deletes the promotion level
        Ack ack = Ack.one(getNamedParameterJdbcTemplate().update(
                SQL.PROMOTION_LEVEL_DELETE,
                params("id", promotionLevelId)
        ));
        // Reordering
        if (ack.isSuccess()) {
            getNamedParameterJdbcTemplate().update(
                    SQL.PROMOTION_LEVEL_UPDATE_LEVEL_NB_AFTER_DELETE,
                    params("levelNb", promotionLevel.getLevelNb())
            );
        }
        // OK
        return ack;
    }

    @Override
    @Transactional
    public void setAutoPromote(int promotionLevelId, boolean flag) {
        getNamedParameterJdbcTemplate().update(
                SQL.PROMOTION_LEVEL_AUTO_PROMOTE,
                params("id", promotionLevelId).addValue("flag", flag)
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TPromotionLevel getByBranchAndName(int branch, String promotionLevel) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROMOTION_LEVEL_BY_BRANCH_AND_NAME,
                params("branch", branch).addValue("name", promotionLevel),
                promotionLevelMapper);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TPromotionLevel> findByBuild(int build) {
        return getNamedParameterJdbcTemplate().query(
                SQL.PROMOTION_LEVEL_FOR_BUILD,
                params("build", build),
                promotionLevelMapper
        );
    }

    @Override
    @Transactional
    public Ack upPromotionLevel(int promotionLevelId) {
        TPromotionLevel promotionLevel = getById(promotionLevelId);
        Integer higherId = getFirstItem(
                SQL.PROMOTION_LEVEL_HIGHER,
                params("branch", promotionLevel.getBranch()).addValue("levelNb", promotionLevel.getLevelNb()),
                Integer.class);
        if (higherId != null) {
            return swapPromotionLevelNb(promotionLevelId, higherId);
        } else {
            return Ack.NOK;
        }
    }

    @Override
    @Transactional
    public Ack downPromotionLevel(int promotionLevelId) {
        TPromotionLevel promotionLevel = getById(promotionLevelId);
        Integer lowerId = getFirstItem(
                SQL.PROMOTION_LEVEL_LOWER,
                params("branch", promotionLevel.getBranch()).addValue("levelNb", promotionLevel.getLevelNb()),
                Integer.class);
        if (lowerId != null) {
            return swapPromotionLevelNb(promotionLevelId, lowerId);
        } else {
            return Ack.NOK;
        }
    }

    protected Ack swapPromotionLevelNb(int aId, int bId) {
        // Loads the level numbers
        NamedParameterJdbcTemplate t = getNamedParameterJdbcTemplate();
        // Gets the order values
        int ordera = t.queryForInt(SQL.PROMOTION_LEVEL_LEVELNB, params("id", aId));
        int orderb = t.queryForInt(SQL.PROMOTION_LEVEL_LEVELNB, params("id", bId));
        // Changes the order
        t.update(SQL.PROMOTION_LEVEL_SET_LEVELNB, params("id", aId).addValue("levelNb", orderb));
        t.update(SQL.PROMOTION_LEVEL_SET_LEVELNB, params("id", bId).addValue("levelNb", ordera));
        // OK
        return Ack.OK;
    }

    @Override
    @Transactional
    public Ack updateImage(int id, byte[] image) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.PROMOTION_LEVEL_IMAGE_UPDATE,
                        params("id", id).addValue("image", image)
                )
        );
    }

    @Override
    @Transactional(readOnly = true)
    public byte[] getImage(int id) {
        return getImage(SQL.PROMOTION_LEVEL_IMAGE, id);
    }
}
