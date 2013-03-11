package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ProjectDao;
import net.ontrack.backend.dao.model.TProject;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProjectJdbcDao extends AbstractJdbcDao implements ProjectDao {

    protected final RowMapper<TProject> projectSummaryMapper = new RowMapper<TProject>() {
        @Override
        public TProject mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new TProject(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
        }
    };

    @Autowired
    public ProjectJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProject> findAll() {
        return getJdbcTemplate().query(
                SQL.PROJECT_LIST,
                projectSummaryMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TProject getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROJECT,
                params("id", id),
                projectSummaryMapper
        );
    }

    @Override
    @Transactional
    public int createProject(String name, String description) {
        return dbCreate(
                SQL.PROJECT_CREATE,
                params("name", name).addValue("description", description));
    }

    @Override
    @Transactional
    public Ack deleteProject(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.PROJECT_DELETE,
                        params("id", id)
                )
        );
    }
}
