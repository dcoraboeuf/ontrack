package net.ontrack.service;

import net.ontrack.service.model.LDAPConfiguration;

import java.util.Map;

public interface AdminService {

    LDAPConfiguration getLDAPConfiguration();

    void saveLDAPConfiguration(LDAPConfiguration configuration);

    String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters);
}
