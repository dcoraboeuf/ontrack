package net.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientNullContentException extends JenkinsClientException {
    public JenkinsClientNullContentException(AbstractHttpMessage request) {
        super(request);
    }
}
