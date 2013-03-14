package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.model.Entity;
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
        Validate.isTrue(list.size() == 2, "List of arguments must contain 2 elements");
        // Input
        Entity entity = Entity.valueOf((String) list.get(0));
        int entityId = Integer.parseInt((String) list.get(1), 10);
        // List of values
        return propertiesService.getPropertyValues(entity, entityId);
    }
}
