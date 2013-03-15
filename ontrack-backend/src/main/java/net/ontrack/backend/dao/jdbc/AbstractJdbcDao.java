package net.ontrack.backend.dao.jdbc;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.Status;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.jdbc.support.GeneratedKeyHolder;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;

// FIXME Caching

public abstract class AbstractJdbcDao extends NamedParameterJdbcDaoSupport {

    private final Function<Object, String> quoteFn = new Function<Object, String>() {
        @Override
        public String apply(Object o) {
            return "'" + o + "'";
        }
    };

    public AbstractJdbcDao(DataSource dataSource) {
        setDataSource(dataSource);
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, Class<T> type) {
        List<T> items = getNamedParameterJdbcTemplate().queryForList(sql, criteria, type);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected <T> T getFirstItem(String sql, MapSqlParameterSource criteria, RowMapper<T> rowMapper) {
        List<T> items = getNamedParameterJdbcTemplate().query(sql, criteria, rowMapper);
        if (items.isEmpty()) {
            return null;
        } else {
            return items.get(0);
        }
    }

    protected Integer getInteger(ResultSet rs, String name) throws SQLException {
        int i = rs.getInt(name);
        if (rs.wasNull()) {
            return null;
        } else {
            return i;
        }
    }

    protected byte[] getImage(String sql, int id) {
        List<byte[]> list = getNamedParameterJdbcTemplate().query(
                sql,
                params("id", id),
                new RowMapper<byte[]>() {
                    @Override
                    public byte[] mapRow(ResultSet rs, int row) throws SQLException, DataAccessException {
                        return rs.getBytes("image");
                    }
                });
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    protected String getStatusesForSQLInClause(Collection<Status> statuses) {
        return StringUtils.join(
                Collections2.transform(statuses, quoteFn),
                ","
        );
    }

    protected int dbCreate(String sql, MapSqlParameterSource params) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        getNamedParameterJdbcTemplate().update(sql, params, keyHolder);
        return keyHolder.getKey().intValue();
    }

    protected MapSqlParameterSource params(String name, Object value) {
        return new MapSqlParameterSource(name, value);
    }
}
