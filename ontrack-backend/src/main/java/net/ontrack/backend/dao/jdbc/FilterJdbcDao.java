package net.ontrack.backend.dao.jdbc;

import net.ontrack.backend.dao.FilterDao;
import net.ontrack.backend.db.SQL;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.BuildFilter;
import net.ontrack.core.model.SavedBuildFilter;
import net.ontrack.dao.AbstractJdbcDao;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.io.IOException;

@Component
public class FilterJdbcDao extends AbstractJdbcDao implements FilterDao {

    private final ObjectMapper objectMapper;

    @Autowired
    public FilterJdbcDao(DataSource dataSource, ObjectMapper objectMapper) {
        super(dataSource);
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public Ack saveFilter(int accountId, int branchId, SavedBuildFilter savedBuildFilter) {
        // Deletes any previous filter with this name
        MapSqlParameterSource params = params("account", accountId)
                .addValue("branch", branchId)
                .addValue("filterName", savedBuildFilter.getFilterName());
        getNamedParameterJdbcTemplate().update(
                SQL.ACCOUNT_FILTER_DELETE,
                params
        );
        // Saves the filter
        return Ack.one(
                getNamedParameterJdbcTemplate().update(
                        SQL.ACCOUNT_FILTER_INSERT,
                        params.addValue("filter", toDB(savedBuildFilter.getFilter()))
                )
        );
    }

    protected String toDB(BuildFilter filter) {
        try {
            return objectMapper.writeValueAsString(filter);
        } catch (IOException e) {
            throw new FilterIOException(e);
        }
    }
}
