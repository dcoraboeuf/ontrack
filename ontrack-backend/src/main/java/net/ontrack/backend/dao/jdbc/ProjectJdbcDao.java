package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.Caches;
import net.ontrack.backend.ProjectAlreadyExistException;
import net.ontrack.backend.dao.ProjectDao;
import net.ontrack.backend.dao.model.TProject;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.dao.AbstractJdbcDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
public class ProjectJdbcDao extends AbstractJdbcDao implements ProjectDao {

    protected final RowMapper<TProject> projectMapper = new RowMapper<TProject>() {
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
                projectMapper
        );
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(Caches.PROJECT)
    public TProject getById(int id) {
        return getNamedParameterJdbcTemplate().queryForObject(
                SQL.PROJECT,
                params("id", id),
                projectMapper
        );
    }

    @Override
    @Transactional
    public int createProject(String name, String description) {
        try {
            return dbCreate(
                    SQL.PROJECT_CREATE,
                    params("name", name).addValue("description", description));
        } catch (DuplicateKeyException ex) {
            throw new ProjectAlreadyExistException(name);
        }
    }

    @Override
    @Transactional
    @CacheEvict(Caches.PROJECT)
    public Ack deleteProject(int id) {
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.PROJECT_DELETE,
                        params("id", id)
                )
        );
    }

    @Override
    @Transactional
    @CacheEvict(value = Caches.PROJECT, key = "#id")
    public Ack updateProject(int id, String name, String description) {
        try {
            return Ack.one(
                    getNamedParameterJdbcTemplate().update(
                            SQL.PROJECT_UPDATE,
                            params("id", id).addValue("name", name).addValue("description", description)
                    )
            );
        } catch (DuplicateKeyException ex) {
            throw new ProjectAlreadyExistException(name);
        }
    }
}
