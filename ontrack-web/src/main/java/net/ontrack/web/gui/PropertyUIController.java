package net.ontrack.web.gui;

import net.ontrack.core.model.Entity;
import net.ontrack.extension.api.PropertiesService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

@Controller
@RequestMapping("/ui/property")
public class PropertyUIController extends AbstractUIController {

    private final PropertiesService propertiesService;

    @Autowired
    public PropertyUIController(ErrorHandler errorHandler, Strings strings, PropertiesService propertiesService) {
        super(errorHandler, strings);
        this.propertiesService = propertiesService;
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
            @PathVariable String name,
            Model model) {
        return propertiesService.editHTML(strings, locale, entity, entityId, extension, name);
    }

}
