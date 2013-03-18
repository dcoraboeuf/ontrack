package net.ontrack.web.gui;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.PropertiesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gui/property")
public class PropertyController {

    private final PropertiesService propertiesService;

    @Autowired
    public PropertyController(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    /**
     * Getting the list of properties for an entity
     */
    @RequestMapping(value = "/{entity}/{entityId:\\d+}", method = RequestMethod.GET)
    public String getProperties(@PathVariable Entity entity, @PathVariable int entityId, Model model) {
        // Model
        model.addAttribute("entity", entity);
        model.addAttribute("entityId", entityId);
        model.addAttribute("properties", propertiesService.getPropertyValuesWithDescriptor(entity, entityId));
        // The view
        return "fragment/properties";
    }

}
