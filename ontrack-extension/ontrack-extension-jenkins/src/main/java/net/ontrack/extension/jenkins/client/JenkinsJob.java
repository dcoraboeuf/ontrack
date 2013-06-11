package net.ontrack.extension.jenkins.client;

import lombok.Data;
import net.ontrack.extension.jenkins.JenkinsJobResult;
import net.ontrack.extension.jenkins.JenkinsJobState;

@Data
public class JenkinsJob {

    private final JenkinsJobResult result;
    private final JenkinsJobState state;

}
