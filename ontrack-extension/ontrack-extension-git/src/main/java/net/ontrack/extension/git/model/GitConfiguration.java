package net.ontrack.extension.git.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

@Data
public class GitConfiguration {

    private final String remote;
    private final String branch;
    private final String tag;
    private final String commitLink;
    private final String fileAtCommitLink;

    public GitConfiguration withDefaults() {
        if (StringUtils.isBlank(branch)) {
            return withDefaultBranch();
        } else {
            return this;
        }
    }

    @JsonIgnore
    public boolean isValid() {
        return StringUtils.isNotBlank(remote);
    }

    public GitConfiguration withDefaultBranch() {
        return new GitConfiguration(remote, "master", tag, commitLink, fileAtCommitLink);
    }
}
