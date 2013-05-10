package net.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientCannotGetContentException extends JenkinsClientException {
    public JenkinsClientCannotGetContentException(AbstractHttpMessage request, Exception e) {
        super(e, request, e);
    }
}
