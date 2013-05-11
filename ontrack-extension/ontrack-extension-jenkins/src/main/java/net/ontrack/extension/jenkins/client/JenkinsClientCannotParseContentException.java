package net.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientCannotParseContentException extends JenkinsClientException {
    public JenkinsClientCannotParseContentException(AbstractHttpMessage request, Exception e) {
        super(e, request);
    }
}
