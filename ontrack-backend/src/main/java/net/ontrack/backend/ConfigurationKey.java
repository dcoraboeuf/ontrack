package net.ontrack.backend;

public enum ConfigurationKey {
    /**
     * LDAP
     */
    LDAP_HOST, LDAP_PORT, LDAP_SEARCH_BASE, LDAP_SEARCH_FILTER, LDAP_USER, LDAP_PASSWORD, LDAP_ENABLED,
    /**
     * Mail
     */
    MAIL_HOST, MAIL_AUTHENTICATION, MAIL_START_TLS, MAIL_USER, MAIL_REPLY_TO_ADDRESS, MAIL_PASSWORD
}
