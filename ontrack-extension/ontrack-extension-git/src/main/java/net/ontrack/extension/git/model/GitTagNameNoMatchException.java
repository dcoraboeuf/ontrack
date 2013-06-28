package net.ontrack.extension.git.model;

import net.ontrack.core.support.InputException;

public class GitTagNameNoMatchException extends InputException {
    public GitTagNameNoMatchException(String tagName, String tag) {
        super(tagName, tag);
    }
}
