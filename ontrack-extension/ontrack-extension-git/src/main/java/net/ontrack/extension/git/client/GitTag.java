package net.ontrack.extension.git.client;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class GitTag {

    private final String name;
    private final DateTime time;

}
