package net.ontrack.service.model;

import lombok.Data;

@Data
public class LDAPConfiguration {

    private boolean enabled;
    private String host;
    private Integer port;
    private String searchBase;
    private String searchFilter;
    private String user;
    private String password;

}
