package net.ontrack.backend.config;

public interface EnvInfoService {

    /**
     * List of profiles for the application
     *
     * @see net.ontrack.core.RunProfile
     */
    String getProfiles();

    /**
     * Version of the application
     */
    String getVersion();
}
