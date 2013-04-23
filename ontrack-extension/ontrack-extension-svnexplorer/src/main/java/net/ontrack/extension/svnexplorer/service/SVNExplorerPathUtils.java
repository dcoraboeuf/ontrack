package net.ontrack.extension.svnexplorer.service;

import net.ontrack.extension.svn.service.model.SVNLocation;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SVNExplorerPathUtils {

    private SVNExplorerPathUtils() {
    }

    public static boolean followsBuildPattern(SVNLocation location, String pathPattern) {
        if (pathPattern.endsWith("@*")) {
            // Removes the last part of the pattern
            String pathOnly = StringUtils.substringBeforeLast(pathPattern, "@");
            // Equality of paths is required
            return StringUtils.equals(location.getPath(), pathOnly);
        } else {
            return Pattern.compile(StringUtils.replace(pathPattern, "*", ".+")).matcher(location.getPath()).matches();
        }
    }

    public static String getBuildName(SVNLocation location, String pathPattern) {
        if (pathPattern.endsWith("@*")) {
            // Removes the last part of the pattern
            String pathOnly = StringUtils.substringBeforeLast(pathPattern, "@");
            // Equality of paths is required
            if (StringUtils.equals(location.getPath(), pathOnly)) {
                return String.valueOf(location.getRevision());
            } else {
                throw new IllegalStateException(String.format("Build path %s@%d does not match pattern %s", location.getPath(), location.getRevision(), pathPattern));
            }
        } else {
            String regexPath;
            int groupIndex;
            if (pathPattern.endsWith("/*")) {
                regexPath = StringUtils.replace(pathPattern, "*", "(.*)");
                groupIndex = 1;
            } else {
                String prefix = StringUtils.substringBeforeLast(pathPattern, "/");
                String suffix = StringUtils.substringAfterLast(pathPattern, "/");
                regexPath = prefix + "/(" + StringUtils.replace(suffix, "*", ".*") + ")";
                groupIndex = 1;
            }
            Matcher matcher = Pattern.compile(regexPath).matcher(location.getPath());
            if (matcher.matches()) {
                return matcher.group(groupIndex);
            } else {
                throw new IllegalStateException(String.format("Build path %s@%d does not match pattern %s", location.getPath(), location.getRevision(), pathPattern));
            }
        }
    }
}
