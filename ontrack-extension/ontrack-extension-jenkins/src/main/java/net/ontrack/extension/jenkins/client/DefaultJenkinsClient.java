package net.ontrack.extension.jenkins.client;

import net.ontrack.extension.jenkins.JenkinsJobState;
import org.springframework.stereotype.Component;

@Component
public class DefaultJenkinsClient implements JenkinsClient {

    @Override
    public JenkinsJobState getJobState(String jenkinsJobUrl) {
        // FIXME Implement net.ontrack.extension.jenkins.client.DefaultJenkinsClient.getJobState
        return JenkinsJobState.RUNNING;
    }
    
}
