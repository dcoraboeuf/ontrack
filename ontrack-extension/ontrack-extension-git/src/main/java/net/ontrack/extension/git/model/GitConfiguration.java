package net.ontrack.extension.git.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GitConfiguration {

    private final String remote;
    private final String branch;
    private final String tag;
    private final String commitLink;
    private final String fileAtCommitLink;

    public static GitConfiguration empty() {
        return new GitConfiguration("", "", "", "", "");
    }

    public String getTag() {
        return tag;
    }

    public String getCommitLink() {
        return commitLink;
    }

    public String getFileAtCommitLink() {
        return fileAtCommitLink;
    }

    public GitConfiguration withDefaults() {
        if (StringUtils.isBlank(branch)) {
            return withDefaultBranch();
        } else {
            return this;
        }
    }

    public GitConfiguration withRemote(String value) {
        if (StringUtils.isNotBlank(value)) {
            return new GitConfiguration(value, branch, tag, commitLink, fileAtCommitLink);
        } else {
            return this;
        }
    }

    public GitConfiguration withBranch(String value) {
        if (StringUtils.isNotBlank(value)) {
            return new GitConfiguration(remote, value, tag, commitLink, fileAtCommitLink);
        } else {
            return this;
        }
    }

    public GitConfiguration withTag(String value) {
        if (StringUtils.isNotBlank(value)) {
            return new GitConfiguration(remote, branch, value, commitLink, fileAtCommitLink);
        } else {
            return this;
        }
    }

    public GitConfiguration withCommitLink(String value) {
        if (StringUtils.isNotBlank(value)) {
            return new GitConfiguration(remote, branch, tag, value, fileAtCommitLink);
        } else {
            return this;
        }
    }

    public GitConfiguration withFileAtCommitLink(String value) {
        if (StringUtils.isNotBlank(value)) {
            return new GitConfiguration(remote, branch, tag, commitLink, value);
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

    public boolean isValidTagName(String tagName) {
        if (StringUtils.isBlank(tag)) {
            return true;
        } else {
            return Pattern.matches(StringUtils.replace(tag, "*", ".*"), tagName);
        }
    }

    public String getBuildNameFromTagName(String tagName) {
        if (StringUtils.isBlank(tag)) {
            return tagName;
        } else {
            Pattern p = Pattern.compile(StringUtils.replace(tag, "*", "(.*)"));
            Matcher m = p.matcher(tagName);
            if (m.matches()) {
                return m.group(1);
            } else {
                throw new GitTagNameNoMatchException(tagName, tag);
            }
        }
    }
}
