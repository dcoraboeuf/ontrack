package net.ontrack.service;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.PasswordChangeForm;
import net.ontrack.service.model.GeneralConfiguration;
import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.service.model.MailConfiguration;

import java.util.Map;

public interface AdminService {

    GeneralConfiguration getGeneralConfiguration();

    void saveGeneralConfiguration(GeneralConfiguration configuration);

    LDAPConfiguration getLDAPConfiguration();

    void saveLDAPConfiguration(LDAPConfiguration configuration);

    MailConfiguration getMailConfiguration();

    void saveMailConfiguration(MailConfiguration configuration);

    String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters);
}
