package net.ontrack.extension.issue;

import net.sf.jstring.support.CoreException;

public class IssueServiceNotFoundException extends CoreException {
    public IssueServiceNotFoundException(String name) {
        super(name);
    }
}
