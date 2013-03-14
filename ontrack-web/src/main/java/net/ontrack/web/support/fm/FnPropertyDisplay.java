package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.service.PropertiesService;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.Validate;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.List;
import java.util.Locale;

public class FnPropertyDisplay implements TemplateMethodModel {

    private final Strings strings;
    private final PropertiesService propertiesService;

    public FnPropertyDisplay(Strings strings, PropertiesService propertiesService) {
        this.strings = strings;
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
        // Gets the locale from the context
        Locale locale = LocaleContextHolder.getLocale();
        // Rendering
        return propertiesService.toHTML(strings, locale, extension, name, value);
    }
}
