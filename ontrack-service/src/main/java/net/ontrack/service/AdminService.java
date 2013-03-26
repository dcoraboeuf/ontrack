package net.ontrack.service;

import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.service.model.MailConfiguration;

import java.util.Map;

public interface AdminService {

    LDAPConfiguration getLDAPConfiguration();

    void saveLDAPConfiguration(LDAPConfiguration configuration);

    MailConfiguration getMailConfiguration();

    void saveMailConfiguration(MailConfiguration configuration);

    String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters);
}
