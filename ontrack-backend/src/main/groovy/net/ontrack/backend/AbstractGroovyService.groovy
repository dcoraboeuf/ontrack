package net.ontrack.backend

import groovy.sql.Sql

import java.lang.invoke.MethodHandleImpl.BindCaller.T

import javax.sql.DataSource
import javax.validation.Validator


abstract class AbstractGroovyService extends AbstractServiceImpl {
	
	protected Sql sql

	AbstractGroovyService(DataSource dataSource, Validator validator) {
		super(dataSource, validator)
		sql = new Sql(dataSource)
	}

}
