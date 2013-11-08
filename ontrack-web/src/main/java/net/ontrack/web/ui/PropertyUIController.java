package net.ontrack.web.ui;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.ontrack.core.model.*;
import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.AuthorizationUtils;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.ui.PropertyUI;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.api.property.PropertyExtensionDescriptor;
import net.ontrack.extension.api.property.PropertyValueWithDescriptor;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/ui/property")
public class PropertyUIController extends AbstractUIController implements PropertyUI {

    private final PropertiesService propertiesService;
    private final SecurityUtils securityUtils;
    private final AuthorizationUtils authorizationUtils;

    @Autowired
    public PropertyUIController(ErrorHandler errorHandler, Strings strings, PropertiesService propertiesService, SecurityUtils securityUtils, AuthorizationUtils authorizationUtils) {
        super(errorHandler, strings);
        this.propertiesService = propertiesService;
        this.securityUtils = securityUtils;
        this.authorizationUtils = authorizationUtils;
    }

    /**
     * Editing a property
     */
    @RequestMapping(value = "/{entity}/{entityId:\\d+}/edit/{extension}/{name:.*}", method = RequestMethod.GET)
    public
    @ResponseBody
    String editProperty(
            Locale locale,
            @PathVariable Entity entity,
            @PathVariable int entityId,
            @PathVariable String extension,
            @PathVariable String name) {
        return propertiesService.editHTML(strings, locale, entity, entityId, extension, name);
    }

    /**
     * Editing a property
     */
    @Override
    @RequestMapping(value = "/{entity}/{entityId:\\d+}/edit/{extension}/{name:.*}", method = RequestMethod.POST)
    public
    @ResponseBody
    Ack saveProperty(
            @PathVariable Entity entity,
            @PathVariable int entityId,
            @PathVariable String extension,
            @PathVariable String name,
            @RequestBody PropertyForm form) {
        return propertiesService.saveProperty(entity, entityId, extension, name, form.getValue());
    }

    /**
     * Getting the list of properties for an entity
     */
    @Override
    @RequestMapping(value = "/{entity}/{entityId:\\d+}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<DisplayablePropertyValue> getProperties(
            final Locale locale,
            @PathVariable final Entity entity,
            @PathVariable final int entityId) {
        // List of defined properties with their value
        List<PropertyValueWithDescriptor> propertyValuesWithDescriptors = propertiesService.getPropertyValuesWithDescriptor(entity, entityId);
        // Filter on visibility
        Collection<PropertyValueWithDescriptor> filteredPropertyValueWithDescriptors = Collections2.filter(
                propertyValuesWithDescriptors,
                new Predicate<PropertyValueWithDescriptor>() {
                    @Override
                    public boolean apply(PropertyValueWithDescriptor propertyValueWithDescriptor) {
                        return isPropertyViewable(propertyValueWithDescriptor.getDescriptor(), entity, entityId);
                    }
                }
        );
        Collection<DisplayablePropertyValue> displayablePropertyValues = Collections2.transform(
                filteredPropertyValueWithDescriptors,
                new Function<PropertyValueWithDescriptor, DisplayablePropertyValue>() {
                    @Override
                    public DisplayablePropertyValue apply(PropertyValueWithDescriptor property) {
                        return new DisplayablePropertyValue(
                                property.getDescriptor().toHTML(strings, locale, property.getValue()),
                                property.getDescriptor().getExtension(),
                                property.getDescriptor().getName(),
                                strings.get(locale, property.getDescriptor().getDisplayNameKey()),
                                property.getDescriptor().getIconPath(),
                                property.getValue(),
                                isPropertyEditable(property.getDescriptor(), entity, entityId)
                        );
                    }
                }
        );
        // OK
        return Lists.newArrayList(displayablePropertyValues);
    }

    @Override
    @RequestMapping(value = "/{entity}", method = RequestMethod.GET)
    public
    @ResponseBody
    List<DisplayableProperty> getPropertyList(final Locale locale, @PathVariable Entity entity) {
        return Lists.transform(
                propertiesService.getProperties(entity),
                new Function<PropertyExtensionDescriptor, DisplayableProperty>() {
                    @Override
                    public DisplayableProperty apply(PropertyExtensionDescriptor descriptor) {
                        return new DisplayableProperty(
                                descriptor.getExtension(),
                                descriptor.getName(),
                                strings.get(locale, descriptor.getDisplayNameKey()),
                                descriptor.getIconPath()
                        );
                    }
                }
        );
    }

    /**
     * Getting the list of properties for an entity
     */
    @Override
    @RequestMapping(value = "/{entity}/{entityId:\\d+}/editable", method = RequestMethod.GET)
    public
    @ResponseBody
    List<EditableProperty> getEditableProperties(
            final Locale locale,
            @PathVariable final Entity entity,
            @PathVariable final int entityId) {
        // List of editable properties for this entity
        List<? extends PropertyExtensionDescriptor> properties = propertiesService.getProperties(entity);
        // Filter on editable state
        ArrayList<PropertyExtensionDescriptor> editablePropertyDescriptors = Lists.newArrayList(
                Iterables.filter(
                        properties,
                        new Predicate<PropertyExtensionDescriptor>() {
                            @Override
                            public boolean apply(PropertyExtensionDescriptor property) {
                                return isPropertyEditable(property, entity, entityId);
                            }
                        }
                )
        );
        // Suitable form for export
        return Lists.transform(
                editablePropertyDescriptors,
                new Function<PropertyExtensionDescriptor, EditableProperty>() {
                    @Override
                    public EditableProperty apply(PropertyExtensionDescriptor descriptor) {
                        String extension = descriptor.getExtension();
                        String propertyName = descriptor.getName();
                        String propertyValue = propertiesService.getPropertyValue(
                                entity,
                                entityId,
                                extension,
                                propertyName);
                        return new EditableProperty(
                                extension,
                                propertyName,
                                strings.get(locale, descriptor.getDisplayNameKey()),
                                descriptor.getIconPath(),
                                propertyValue,
                                propertiesService.toHTML(
                                        strings,
                                        locale,
                                        extension,
                                        propertyName,
                                        propertyValue
                                ),
                                propertiesService.editHTML(
                                        strings,
                                        locale,
                                        entity,
                                        entityId,
                                        extension,
                                        propertyName
                                )
                        );
                    }
                }
        );
    }

    @Override
    @RequestMapping(value = "/{entity}/{entityId:\\d+}/{extension}/{name}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getPropertyValue(@PathVariable Entity entity, @PathVariable int entityId, @PathVariable String extension, @PathVariable String name) {
        return propertiesService.getPropertyValue(entity, entityId, extension, name);
    }

    private boolean isPropertyViewable(PropertyExtensionDescriptor descriptor, Entity entity, int entityId) {
        // Gets the security policy
        AuthorizationPolicy policy = descriptor.getViewingAuthorizationPolicy(entity);
        // Applies the security policy
        return authorizationUtils.applyPolicy(policy, entity, entityId);
    }

    private boolean isPropertyEditable(PropertyExtensionDescriptor descriptor, Entity entity, int entityId) {
        // Gets the security policy
        AuthorizationPolicy policy = descriptor.getEditingAuthorizationPolicy(entity);
        // Applies the security policy
        return authorizationUtils.applyPolicy(policy, entity, entityId);
    }

}
