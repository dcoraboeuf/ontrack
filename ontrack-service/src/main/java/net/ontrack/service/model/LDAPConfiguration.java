package net.ontrack.service.model;

import lombok.Data;
import net.ontrack.service.validation.LDAPConfigurationValidation;

@Data
public class LDAPConfiguration implements LDAPConfigurationValidation {

    private boolean enabled;
    private String host;
    private Integer port;
    private String searchBase;
    private String searchFilter;
    private String user;
    private String password;

}
