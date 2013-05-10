package net.ontrack.extension.jenkins.client;

import net.sf.jstring.support.CoreException;

public abstract class JenkinsClientException extends CoreException {

    public JenkinsClientException(Object... params) {
        super(params);
    }

    public JenkinsClientException(Throwable error, Object... params) {
        super(error, params);
    }
}
