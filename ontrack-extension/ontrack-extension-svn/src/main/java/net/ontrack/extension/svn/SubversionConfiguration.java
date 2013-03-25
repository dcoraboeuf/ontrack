package net.ontrack.extension.svn;

import lombok.Data;

@Data
public class SubversionConfiguration {

    private String url;
    private String user;
    private String password;
    private String branchPattern;
    private String tagPattern;
    private String tagFilterPattern;

}
