package net.ontrack.extension.jenkins.client;

import net.ontrack.extension.jenkins.JenkinsConfigurationExtension;

public interface JenkinsClient {

    JenkinsJob getJob(JenkinsConfigurationExtension configuration, String jenkinsJobUrl, boolean details);

}
