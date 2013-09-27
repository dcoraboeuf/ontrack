package net.ontrack.extension.jenkins.client;

import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

@Data
public class JenkinsBuildLink {

    private final int number;
    private final String url;

    @JsonIgnore
    public String getConsoleUrl() {
        return url + "console";
    }
}
