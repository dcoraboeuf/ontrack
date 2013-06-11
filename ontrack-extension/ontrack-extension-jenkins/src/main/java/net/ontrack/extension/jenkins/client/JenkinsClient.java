package net.ontrack.extension.jenkins.client;

import net.ontrack.extension.jenkins.JenkinsJobResult;
import net.ontrack.extension.jenkins.JenkinsJobState;

public interface JenkinsClient {

    JenkinsJobState getJobState(String jenkinsJobUrl);

    JenkinsJobResult getJobResult(String jenkinsJobUrl);

}
