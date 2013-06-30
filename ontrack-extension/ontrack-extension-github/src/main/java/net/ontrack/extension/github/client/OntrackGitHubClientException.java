package net.ontrack.extension.github.client;

import net.sf.jstring.support.CoreException;

import java.io.IOException;

public class OntrackGitHubClientException extends CoreException {
    public OntrackGitHubClientException(IOException e) {
        super(e, e);
    }
}
