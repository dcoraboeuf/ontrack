package net.ontrack.backend.extension;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.ontrack.backend.dao.ConfigurationDao;
import net.ontrack.backend.db.StartupService;
import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.Extension;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.ExtensionNotFoundException;
import net.ontrack.extension.api.action.ActionExtension;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtension;
import net.ontrack.extension.api.configuration.ConfigurationExtensionNotFoundException;
import net.ontrack.extension.api.decorator.EntityDecorator;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.property.PropertyExtensionNotFoundException;
import net.sf.jstring.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Implementation of the extension manager.
 */
@Component
public class DefaultExtensionManager implements ExtensionManager, StartupService {

    private final ApplicationContext applicationContext;
    private final Strings strings;
    private final ConfigurationDao configurationDao;
    private Map<String, Extension> extensionIndex;
    private Map<String, Map<String, PropertyExtensionDescriptor>> propertyIndex;
    private Map<String, Map<String, ConfigurationExtension>> configurationIndex;
    private Collection<ActionExtension> topLevelActions;
    private Collection<ActionExtension> diffActions;
    private Collection<EntityDecorator> decorators;

    @Autowired
    public DefaultExtensionManager(ApplicationContext applicationContext, Strings strings, ConfigurationDao configurationDao) {
        this.applicationContext = applicationContext;
        this.strings = strings;
        this.configurationDao = configurationDao;
    }

    @Override
    public int startupOrder() {
        return 2;
    }

    @Override
    public void start() {
        Logger logger = LoggerFactory.getLogger(ExtensionManager.class);

        /**
         * Gets the list of extensions
         */
        Collection<Extension> extensions = applicationContext.getBeansOfType(Extension.class).values();

        /**
         * Indexation of extensions
         */
        logger.info("[extension] Indexing extensions");
        extensionIndex = new TreeMap<>();
        propertyIndex = new HashMap<>();
        configurationIndex = new HashMap<>();
        topLevelActions = new ArrayList<>();
        diffActions = new ArrayList<>();
        decorators = new ArrayList<>();
        for (Extension extension : extensions) {
            String extensionName = extension.getName();
            logger.info("[extension] Extension={}", extensionName);
            extensionIndex.put(extensionName, extension);

            /**
             * Indexation of properties
             */

            for (PropertyExtensionDescriptor descriptor : extension.getPropertyExtensionDescriptors()) {
                String name = descriptor.getName();
                // Logging
                logger.info("[extension] Property extension={}, name={}", extensionName, name);
                // Index per extension
                Map<String, PropertyExtensionDescriptor> extensionPropertyIndex = propertyIndex.get(extensionName);
                if (extensionPropertyIndex == null) {
                    extensionPropertyIndex = new HashMap<>();
                    propertyIndex.put(extensionName, extensionPropertyIndex);
                }
                // Index per name
                if (extensionPropertyIndex.containsKey(name)) {
                    logger.warn("[extension] Property name {} already defined for extension {}", name, extensionName);
                }
                extensionPropertyIndex.put(name, descriptor);
            }

            /**
             * Indexation of configurations
             */
            for (ConfigurationExtension configurationExtension : extension.getConfigurationExtensions()) {
                // Logging
                logger.info("[extension] Configuration extension={}, configuration={}", extensionName, configurationExtension);
                // Index per extension
                Map<String, ConfigurationExtension> extensionConfigurationIndex = configurationIndex.get(extensionName);
                if (extensionConfigurationIndex == null) {
                    extensionConfigurationIndex = new TreeMap<>();
                    configurationIndex.put(extensionName, extensionConfigurationIndex);
                }
                // Adds to the list
                extensionConfigurationIndex.put(configurationExtension.getName(), configurationExtension);
            }

            /**
             * Indexation of actions
             */
            topLevelActions.addAll(extension.getTopLevelActions());
            diffActions.addAll(extension.getDiffActions());

            /**
             * Indexation of decorators
             */
            decorators.addAll(extension.getDecorators());

        }
    }

    @Override
    public Collection<? extends ActionExtension> getTopLevelActions() {
        return topLevelActions;
    }

    @Override
    public Collection<? extends ActionExtension> getDiffActions() {
        return diffActions;
    }

    @Override
    public Collection<? extends EntityDecorator> getDecorators() {
        return decorators;
    }

    @Override
    public Collection<EntityActionExtension<ProjectSummary>> getProjectActions() {
        Collection<EntityActionExtension<ProjectSummary>> actions = new ArrayList<>();
        for (Extension extension : extensionIndex.values()) {
            Collection<? extends EntityActionExtension> entityActions = extension.getEntityActions();
            for (EntityActionExtension entityAction : entityActions) {
                if (entityAction.getScope() == Entity.PROJECT) {
                    actions.add(entityAction);
                }
            }
        }
        return actions;
    }

    /**
     * Enabling an extension must enable all its dependencies
     */
    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack enableExtension(String name) {
        // Set of extensions to enable
        Set<String> target = new HashSet<>();
        // Stack of extensions to enable
        Stack<String> stack = new Stack<>();
        stack.push(name);
        // Collects the extensions to enable
        while (!stack.isEmpty()) {
            String extensionName = stack.pop();
            // Gets the extension
            Extension extension = extensionIndex.get(extensionName);
            if (extension != null && !target.contains(extensionName)) {
                target.add(extensionName);
                // Adds the dependencies to the stack
                stack.addAll(extension.getDependencies());
            }
        }
        // Enables all selected extensions
        for (String extensionName : target) {
            configurationDao.setValue("extension." + extensionName, "true");
        }
        // OK
        return Ack.OK;
    }

    /**
     * Disabling an extension must disable all extensions that depend on it.
     */
    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public Ack disableExtension(String name) {
        // Gets the tree of dependencies
        TreeMap<String, ExtensionNode> extensionTreeMap = getExtensionTreeMap();
        // Set of extensions to disable
        Set<String> target = new HashSet<>();
        // Stack of extensions to disable
        Stack<String> stack = new Stack<>();
        stack.push(name);
        // Collects the extensions to disable
        while (!stack.isEmpty()) {
            String extensionName = stack.pop();
            // Gets the extension
            ExtensionNode extension = extensionTreeMap.get(extensionName);
            if (extension != null && !target.contains(extensionName)) {
                target.add(extensionName);
                // Adds the dependencies to the stack
                stack.addAll(extension.getRequirementFor());
            }
        }
        // Enables all selected extensions
        for (String extensionName : target) {
            configurationDao.setValue("extension." + extensionName, "false");
        }
        // OK
        return Ack.OK;
    }

    @Override
    @Secured(SecurityRoles.ADMINISTRATOR)
    public List<ExtensionSummary> getExtensionTree(final Locale locale) {
        TreeMap<String, ExtensionNode> extensionIndex = getExtensionTreeMap();
        return Lists.transform(
                Lists.newArrayList(extensionIndex.values()),
                new Function<ExtensionNode, ExtensionSummary>() {
                    @Override
                    public ExtensionSummary apply(ExtensionNode node) {
                        return new ExtensionSummary(
                                node.getName(),
                                strings.get(locale, "extension." + node.getName()),
                                isExtensionEnabled(node.getName()),
                                node.getDependencies(),
                                node.getRequirementFor()
                        );
                    }
                }
        );
    }

    @Override
    public boolean isExtensionEnabled(String name) {
        return "true".equals(configurationDao.getValue("extension." + name));
    }

    private TreeMap<String, ExtensionNode> getExtensionTreeMap() {
        // Gets the list of extensions
        List<Extension> extensions = new ArrayList<>(extensionIndex.values());
        // Sorts by name
        Collections.sort(extensions, new Comparator<Extension>() {
            @Override
            public int compare(Extension o1, Extension o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });
        // Index of extension summaries
        TreeMap<String, ExtensionNode> extensionIndex = new TreeMap<>(Maps.uniqueIndex(
                Lists.transform(
                        extensions,
                        new Function<Extension, ExtensionNode>() {
                            @Override
                            public ExtensionNode apply(Extension extension) {
                                return new ExtensionNode(extension.getName());
                            }
                        }
                ),
                new Function<ExtensionNode, String>() {
                    @Override
                    public String apply(ExtensionNode extensionNode) {
                        return extensionNode.getName();
                    }
                }));
        // Tree of dependencies
        for (Extension extension : extensions) {
            String name = extension.getName();
            ExtensionNode extensionSummary = extensionIndex.get(name);
            Collection<String> dependencies = extension.getDependencies();
            for (String dependency : dependencies) {
                ExtensionNode dependencyExtension = extensionIndex.get(dependency);
                extensionSummary.dependsOn(dependencyExtension);
            }
        }
        return extensionIndex;
    }

    @Override
    public List<PropertyExtensionDescriptor> getPropertyExtensionDescriptors(Entity entity) {
        List<PropertyExtensionDescriptor> list = new ArrayList<>();
        for (Map<String, PropertyExtensionDescriptor> extensionIndex : propertyIndex.values()) {
            for (PropertyExtensionDescriptor descriptor : extensionIndex.values()) {
                if (descriptor.getScope().contains(entity)) {
                    list.add(descriptor);
                }
            }
        }
        return list;
    }

    @Override
    public Collection<? extends ConfigurationExtension> getConfigurationExtensions() {
        Collection<ConfigurationExtension> list = new ArrayList<>();
        for (Map<String, ConfigurationExtension> index : configurationIndex.values()) {
            for (ConfigurationExtension configurationExtension : index.values()) {
                list.add(configurationExtension);
            }
        }
        return list;
    }

    @Override
    public <T extends ConfigurationExtension> T getConfigurationExtension(String extension, String name) {
        Map<String, ConfigurationExtension> extensionIndex = configurationIndex.get(extension);
        if (extension != null) {
            ConfigurationExtension configurationExtension = extensionIndex.get(name);
            if (configurationExtension != null) {
                return (T) configurationExtension;
            }
        }
        throw new ConfigurationExtensionNotFoundException(extension, name);
    }

    @Override
    public Collection<? extends Extension> getExtensions() {
        return extensionIndex.values();
    }

    @Override
    public <T extends Extension> T getExtension(String name) throws ExtensionNotFoundException {
        Extension extension = extensionIndex.get(name);
        if (extension != null) {
            return (T) extension;
        } else {
            throw new ExtensionNotFoundException(name);
        }
    }

    @Override
    public <T extends PropertyExtensionDescriptor> T getPropertyExtensionDescriptor(String extension, String name) throws PropertyExtensionNotFoundException {
        Map<String, PropertyExtensionDescriptor> extensionIndex = propertyIndex.get(extension);
        if (extensionIndex != null) {
            PropertyExtensionDescriptor descriptor = extensionIndex.get(name);
            if (descriptor != null) {
                return (T) descriptor;
            }
        }
        throw new PropertyExtensionNotFoundException(extension, name);
    }

}
