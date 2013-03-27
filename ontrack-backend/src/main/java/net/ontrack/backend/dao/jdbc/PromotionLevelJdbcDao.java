package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.PromotionLevelDao;
import net.ontrack.backend.dao.model.TPromotionLevel;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
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
            return new TPromotionLevel(rs.getInt("id"), rs.getInt("branch"), rs.getInt("levelNb"), rs.getString("name"), rs.getString("description"));
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
        return dbCreate(
                SQL.PROMOTION_LEVEL_CREATE,
                params("branch", branch)
                        .addValue("name", name)
                        .addValue("description", description)
                        .addValue("levelNb", levelNb));
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
