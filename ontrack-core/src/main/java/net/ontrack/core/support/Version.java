package net.ontrack.core.support;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Version implements Comparable<Version> {

    public static final Pattern REGEX = Pattern.compile("(\\d+)\\.(\\d+)(\\.(\\d+))?");

    public static Version of(String value) {
        if (StringUtils.isBlank(value)) {
            throw new VersionBlankException();
        } else {
            Matcher matcher = REGEX.matcher(value);
            if (matcher.matches()) {
                int major = Integer.parseInt(matcher.group(1), 10);
                int minor = Integer.parseInt(matcher.group(2), 10);
                int patch = 0;
                String patchValue = matcher.group(4);
                if (StringUtils.isNotBlank(patchValue)) {
                    patch = Integer.parseInt(patchValue, 10);
                }
                return new Version(major, minor, patch);
            } else {
                throw new VersionFormatException(value);
            }
        }
    }

    private final int major;
    private final int minor;
    private final int patch;

    public Version(int major) {
        this(major, 0, 0);
    }

    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    @Override
    public String toString() {
        if (patch == 0) {
            return String.format("%d.%d", major, minor);
        } else {
            return String.format("%d.%d.%d", major, minor, patch);
        }
    }

    @Override
    public int compareTo(Version o) {
        if (this.major == o.major) {
            if (this.minor == o.minor) {
                return this.patch - o.patch;
            } else {
                return this.minor - o.minor;
            }
        } else {
            return this.major - o.major;
        }
    }
}
