package net.ontrack.service;

public interface GUIService {

    /**
     * Given an URI for a resource, returns the absolute URL that allows to access to it
     * through the GUI.
     */
    String toGUI(String uri);
}
