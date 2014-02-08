package net.ontrack.extension.api.support;

/**
 * Implement this interface and declare the implementation as a <code>@Component</code>
 * if you want to execute some code at start-up, after the database has been initialized
 * or migrated.
 */
public interface StartupService {

    int startupOrder();

    void start();

}
