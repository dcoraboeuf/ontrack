package net.ontrack.dao;

import javax.sql.DataSource;

public abstract class AbstractDBInitConfig implements DBInitConfig {

    protected final DataSource dataSource;

    protected AbstractDBInitConfig(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}
