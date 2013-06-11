package net.ontrack.extension.jenkins.client;

public interface JenkinsClient {

    JenkinsJob getJob(String jenkinsJobUrl);

}
