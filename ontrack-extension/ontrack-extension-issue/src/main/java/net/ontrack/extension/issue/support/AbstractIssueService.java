package net.ontrack.extension.issue.support;

import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.issue.IssueService;

/**
 * Convenient implementation for most of the issue services.
 */
public abstract class AbstractIssueService implements IssueService {

    private final String id;
    private final String name;
    private final String extension;
    private final ExtensionManager extensionManager;

    /**
     * Constructor.
     *
     * @param id               The unique ID for this service.
     * @param name             The display name for this service.
     * @param extension        The extension this service is linked to.
     * @param extensionManager The extension manager.
     */
    protected AbstractIssueService(String id, String name, String extension, ExtensionManager extensionManager) {
        this.id = id;
        this.name = name;
        this.extension = extension;
        this.extensionManager = extensionManager;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isEnabled() {
        return extensionManager.isExtensionEnabled(extension);
    }
}
