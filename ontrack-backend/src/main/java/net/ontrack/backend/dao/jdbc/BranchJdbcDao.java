package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.Caches;
import net.ontrack.backend.dao.BranchDao;
import net.ontrack.backend.dao.model.TBranch;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class BranchJdbcDao extends AbstractJdbcDao implements BranchDao {

    protected final RowMapper<TBranch> branchMapper = new RowMapper<TBranch>() {
        @Override
        public TBranch mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TBranch(rs.getInt("id"), rs.getInt("project"), rs.getString("name"), rs.getString("description"));
        }
    };

    @Autowired
    public BranchJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TBranch> findByProject(int project) {
        return getNamedParameterJdbcTemplate().query(
                SQL.BRANCH_LIST,
                params("project", project),
                branchMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(Caches.BRANCH)
    public TBranch getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.BRANCH,
                params("id", id),
                branchMapper
        );
    }

    @Override
    @Transactional
    public int createBranch(int project, String name, String description) {
        return dbCreate(
                SQL.BRANCH_CREATE,
                params("project", project).addValue("name", name).addValue("description", description));
    }

    @Override
    @Transactional
    @CacheEvict(Caches.BRANCH)
    public Ack deleteBranch(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.BRANCH_DELETE,
                        params("id", id)
                )
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = Caches.BRANCH, key = "#id")
    public Ack updateBranch(int id, String name, String description) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.BRANCH_UPDATE,
                        params("id", id).addValue("name", name).addValue("description", description)
                )
        );
    }
}
