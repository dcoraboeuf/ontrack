package net.ontrack.web.gui;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.PropertiesService;
import net.ontrack.extension.api.PropertyExtensionDescriptor;
import net.ontrack.extension.api.PropertyValueWithDescriptor;
import net.ontrack.web.gui.model.GUIPropertyValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/gui/property")
public class PropertyController {

    private final PropertiesService propertiesService;
    private final SecurityUtils securityUtils;

    @Autowired
    public PropertyController(PropertiesService propertiesService, SecurityUtils securityUtils) {
        this.propertiesService = propertiesService;
        this.securityUtils = securityUtils;
    }

    /**
     * Getting the list of properties for an entity
     */
    @RequestMapping(value = "/{entity}/{entityId:\\d+}", method = RequestMethod.GET)
    public String getProperties(@PathVariable final Entity entity, @PathVariable int entityId, Model model) {
        // Model
        model.addAttribute("entity", entity);
        model.addAttribute("entityId", entityId);
        // List of defined properties with their value
        model.addAttribute("properties",
                Lists.transform(
                        propertiesService.getPropertyValuesWithDescriptor(entity, entityId),
                        new Function<PropertyValueWithDescriptor, GUIPropertyValue>() {
                            @Override
                            public GUIPropertyValue apply(PropertyValueWithDescriptor property) {
                                return new GUIPropertyValue(
                                        property,
                                        isPropertyEditable(property.getDescriptor(), entity)
                                );
                            }
                        }
                ));
        // List of editable properties for this entity
        List<PropertyExtensionDescriptor> properties = propertiesService.getProperties(entity);
        // Filter on editable state
        List<PropertyExtensionDescriptor> editableProperties =
                Lists.newArrayList(
                        Iterables.filter(
                                properties,
                                new Predicate<PropertyExtensionDescriptor>() {
                                    @Override
                                    public boolean apply(PropertyExtensionDescriptor property) {
                                        return isPropertyEditable(property, entity);
                                    }
                                }
                        )
                );
        model.addAttribute("editableProperties", editableProperties);
        // The view
        return "fragment/properties";
    }

    private boolean isPropertyEditable(PropertyExtensionDescriptor descriptor, Entity entity) {
        String role = descriptor.getRoleForEdition(entity);
        return role != null && securityUtils.hasRole(role);
    }

}
