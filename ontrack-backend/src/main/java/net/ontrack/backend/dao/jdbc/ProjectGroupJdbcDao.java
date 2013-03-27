package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.ProjectGroupDao;
import net.ontrack.backend.dao.model.TProjectGroup;
import net.ontrack.backend.db.SQL;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProjectGroupJdbcDao extends AbstractJdbcDao implements ProjectGroupDao {

    @Autowired
    public ProjectGroupJdbcDao(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TProjectGroup> findAll() {
        return getJdbcTemplate().query(
                SQL.PROJECT_GROUP_LIST,
                new RowMapper<TProjectGroup>() {
                    @Override
                    public TProjectGroup mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new TProjectGroup(rs.getInt("id"), rs.getString("name"), rs.getString("description"));
                    }
                });
    }

    @Override
    @Transactional
    public int createGroup(String name, String description) {
        return dbCreate(
                SQL.PROJECT_GROUP_CREATE,
                params("name", name).addValue("description", description));
    }
}
