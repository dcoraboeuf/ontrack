package net.ontrack.service;

import net.ontrack.service.model.GeneralConfiguration;
import net.ontrack.service.model.LDAPConfiguration;
import net.ontrack.service.model.MailConfiguration;

public interface AdminService {

    GeneralConfiguration getGeneralConfiguration();

    void saveGeneralConfiguration(GeneralConfiguration configuration);

    LDAPConfiguration getLDAPConfiguration();

    void saveLDAPConfiguration(LDAPConfiguration configuration);

    MailConfiguration getMailConfiguration();

    void saveMailConfiguration(MailConfiguration configuration);
}
