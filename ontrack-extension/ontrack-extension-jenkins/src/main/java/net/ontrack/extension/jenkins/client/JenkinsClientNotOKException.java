package net.ontrack.extension.jenkins.client;

import org.apache.http.message.AbstractHttpMessage;

public class JenkinsClientNotOKException extends JenkinsClientException {
    public JenkinsClientNotOKException(AbstractHttpMessage request, int statusCode) {
        super(request, statusCode);
    }
}
