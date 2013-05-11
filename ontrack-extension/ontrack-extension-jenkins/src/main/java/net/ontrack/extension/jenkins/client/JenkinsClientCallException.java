package net.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientCallException extends JenkinsClientException {
    public JenkinsClientCallException(AbstractHttpMessage request, Exception e) {
        super(e, request, e);
    }
}
