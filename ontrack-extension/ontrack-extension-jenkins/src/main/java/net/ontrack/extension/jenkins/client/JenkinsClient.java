package net.ontrack.extension.jenkins.client;

import net.ontrack.extension.jenkins.JenkinsJobState;

public interface JenkinsClient {

    JenkinsJobState getJobState(String jenkinsJobUrl);

}
