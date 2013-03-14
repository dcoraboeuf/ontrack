package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.service.PropertiesService;
import org.apache.commons.lang3.Validate;

import java.util.List;

public class FnPropertyDisplay implements TemplateMethodModel {

    private final PropertiesService propertiesService;

    public FnPropertyDisplay(PropertiesService propertiesService) {
        this.propertiesService = propertiesService;
    }

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 3, "List of arguments must contain 3 elements");
        // Input
        String extension = (String) list.get(0);
        String name = (String) list.get(1);
        String value = (String) list.get(2);
        // Rendering
        return propertiesService.toHTML(extension, name, value);
    }
}
