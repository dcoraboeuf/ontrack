package net.ontrack.core.support;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class Version implements Comparable<Version> {

    public static final Pattern REGEX = Pattern.compile("(\\d+)\\.(\\d+)");

    public static Version of(String value) {
        if (StringUtils.isBlank(value)) {
            throw new VersionBlankException();
        } else {
            Matcher matcher = REGEX.matcher(value);
            if (matcher.matches()) {
                int major = Integer.parseInt(matcher.group(1), 10);
                int minor = Integer.parseInt(matcher.group(2), 10);
                return new Version(major, minor);
            } else {
                throw new VersionFormatException(value);
            }
        }
    }

    private final int major;
    private final int minor;

    @Override
    public String toString() {
        return String.format("%d.%d", major, minor);
    }

    @Override
    public int compareTo(Version o) {
        if (this.major == o.major) {
            return this.minor - o.minor;
        } else {
            return this.major - o.major;
        }
    }
}
