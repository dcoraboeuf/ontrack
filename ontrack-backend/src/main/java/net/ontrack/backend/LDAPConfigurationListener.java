package net.ontrack.backend;

import net.ontrack.service.model.LDAPConfiguration;

/**
 * Listens to any change into the LDAP configuration.
 */
public interface LDAPConfigurationListener {

    void onLDAPConfigurationChanged (LDAPConfiguration configuration);

}
