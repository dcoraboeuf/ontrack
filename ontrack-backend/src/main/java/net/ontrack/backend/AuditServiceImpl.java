package net.ontrack.backend;

import javax.sql.DataSource;

import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.service.AuditService;
import net.ontrack.service.model.Audited;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcDaoSupport;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditServiceImpl extends NamedParameterJdbcDaoSupport implements AuditService {
	
	@Autowired
	public AuditServiceImpl(DataSource dataSource) {
		setDataSource(dataSource);
	}

	@Override
	@Transactional
	public void audit(boolean creation, Audited audited, int id) {
		MapSqlParameterSource params = new MapSqlParameterSource("id", id);
		params.addValue("author", "");
		params.addValue("author_id", null);
		params.addValue("audit_timestamp", SQLUtils.toTimestamp(SQLUtils.now()));
		params.addValue("audit_creation", creation);
		getNamedParameterJdbcTemplate().update(
			String.format(SQL.AUDIT_CREATE, audited.name()),
			params);
	}

}
