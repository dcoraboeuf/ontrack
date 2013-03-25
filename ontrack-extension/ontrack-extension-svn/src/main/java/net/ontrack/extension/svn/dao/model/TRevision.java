package net.ontrack.extension.svn.dao.model;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class TRevision {

    private final long revision;
    private final String author;
    private final DateTime creation;
    private final String message;
    private final String branch;

}
