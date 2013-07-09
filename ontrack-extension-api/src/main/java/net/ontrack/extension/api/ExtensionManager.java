package net.ontrack.extension.api;

import net.ontrack.core.model.*;
import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.property.PropertyExtensionNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public interface ExtensionManager {

    /**
     * Gets the list of extensions
     */
    Collection<? extends Extension> getExtensions();

    /**
     * Gets an extension by its name
     *
     * @throws ExtensionNotFoundException If no extension with this name is defined
     */
    <T extends Extension> T getExtension(String name) throws ExtensionNotFoundException;

    /**
     * Gets a property extension descriptor associated with this extension and this name.
     *
     * @param extension Extension ID
     * @param name      Property name
     * @return Descriptor if found
     * @throws net.ontrack.extension.api.property.PropertyExtensionNotFoundException
     *          If not found
     */
    <T extends PropertyExtensionDescriptor> T getPropertyExtensionDescriptor(String extension, String name) throws PropertyExtensionNotFoundException;

    /**
     * Returns the list of properties applicable for this entity
     */
    List<? extends PropertyExtensionDescriptor> getPropertyExtensionDescriptors(Entity entity);

    /**
     * Gets the list of all configuration extensions
     */
    Collection<? extends ConfigurationExtension> getConfigurationExtensions();

    /**
     * Gets a configuration extension by its extension ID and name
     */
    <T extends ConfigurationExtension> T getConfigurationExtension(String extension, String name);

    /**
     * Saving the configuration
     */
    String saveExtensionConfiguration(String extension, String name, Map<String, String> parameters);

    /**
     * Lists of actions that are available at the top level of the application. They are normally
     * display in the contextual menu for the user.
     */
    Collection<? extends TopActionExtension> getTopLevelActions();

    /**
     * Lists of actions that can be defined for the difference between two builds
     */
    Collection<? extends ActionExtension> getDiffActions();

    /**
     * List of decorators
     */
    Collection<? extends EntityDecorator> getDecorators();

    /**
     * Gets the list of actions that apply to a project
     */
    Collection<EntityActionExtension<ProjectSummary>> getProjectActions();

    /**
     * Gets the list of actions that apply to a branch
     */
    Collection<EntityActionExtension<BranchSummary>> getBranchActions();

    /**
     * Gets the list of extensions together with their dependencies
     */
    List<ExtensionSummary> getExtensionTree(Locale locale);

    /**
     * Enabling an extension
     */
    Ack enableExtension(String name);

    /**
     * Disabling an extension
     */
    Ack disableExtension(String name);

    /**
     * Is an extension enabled?
     */
    boolean isExtensionEnabled(String name);
}
