package net.ontrack.extension.git.client;

import lombok.Data;
import org.joda.time.DateTime;

@Data
public class GitTag implements Comparable<GitTag> {

    private final String name;
    private final DateTime time;

    @Override
    public int compareTo(GitTag o) {
        return this.time.compareTo(o.time);
    }
}
