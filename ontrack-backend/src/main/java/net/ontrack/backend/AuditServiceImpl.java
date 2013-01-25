package net.ontrack.backend;

import static java.lang.String.format;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import net.ontrack.backend.db.SQL;
import net.ontrack.backend.db.SQLUtils;
import net.ontrack.service.AuditService;
import net.ontrack.service.model.Audit;
import net.ontrack.service.model.Audited;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.RowMapper;
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
			format(SQL.AUDIT_CREATE, audited.name()),
			params);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<Audit> all(int offset, int count) {
		return getNamedParameterJdbcTemplate().query(
			SQL.AUDIT_ALL,
			new MapSqlParameterSource("offset", offset).addValue("count", count),
			new RowMapper<Audit>() {
				@Override
				public Audit mapRow(ResultSet rs, int rowNum) throws SQLException {
					return createAudit(rs);
				}
			});
	}

	protected Audit createAudit(ResultSet rs) throws SQLException {
		// General
		int id = rs.getInt("id");
		DateTime timestamp = SQLUtils.getDateTime(rs, "audit_timestamp");
		boolean creation = rs.getBoolean("audit_creation");
		// TODO Author
		// Audited entity
		Audited audited = null;
		int auditedId = 0;
		for (Audited candidate: Audited.values()) {
			int candidateId = rs.getInt(candidate.name());
			if (!rs.wasNull()) {
				audited = candidate;
				auditedId = candidateId;
				break;
			}
		}
		// Test of the audited entity
		if (audited == null) {
			throw new AuditNotRelatedException(id);
		} else {
			// Audited name
			String auditedName = getAuditedName(audited, auditedId);
			// OK
			return new Audit(id, timestamp, creation, audited, auditedId, auditedName);
		}
	}

	protected String getAuditedName(Audited audited, int auditedId) {
		return getNamedParameterJdbcTemplate().queryForObject(
			format(SQL.AUDIT_NAME, audited.nameColumn(), audited.name()),
			new MapSqlParameterSource("id", auditedId),
			String.class);
			
	}

}
