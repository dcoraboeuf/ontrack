package net.ontrack.backend.search;

import net.ontrack.service.GUIService;
import net.ontrack.service.SearchProvider;

public abstract class AbstractEntitySearchProvider implements SearchProvider {

    private final GUIService guiService;

    protected AbstractEntitySearchProvider(GUIService guiService) {
        this.guiService = guiService;
    }

    protected String guiPath(String path, Object... params) {
        return guiService.toGUI(String.format(path, params));
    }
}
