package net.ontrack.extension.jira.tx;

import net.sf.jstring.support.CoreException;

import java.net.URISyntaxException;

public class JIRAConnectionException extends CoreException {
    public JIRAConnectionException(String url, URISyntaxException ex) {
        super(url, ex);
    }
}
