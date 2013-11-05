package net.ontrack.extension.jenkins.client;

import net.ontrack.core.support.InputException;

public class JenkinsClientNotFoundException extends InputException {
    public JenkinsClientNotFoundException(String url) {
        super(url);
    }
}
