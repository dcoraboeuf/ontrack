package net.ontrack.extension.svn;

public interface SubversionService {

    /**
     * Gets the absolute URL that for a path in the subversion repository
     */
    String getURL(String path);

    /**
     * Gets the browsing URL that for a path in the subversion repository
     */
    String getBrowsingURL(String path);
}
