package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.extension.api.Extension;
import net.ontrack.extension.api.ExtensionManager;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

public class FnExtensionStyles implements TemplateMethodModel {

    private final ExtensionManager extensionManager;

    @Autowired
    public FnExtensionStyles(ExtensionManager extensionManager) {
        this.extensionManager = extensionManager;
    }

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 1, "List of arguments must contain 1 element");
        // Gets the scope
        String scope = (String) list.get(0);
        // List of style paths
        Collection<String> paths = new ArrayList<>();
        // For all extensions
        for (Extension extension : extensionManager.getExtensions()) {
            String path = extension.getExtensionStyle(scope);
            if (StringUtils.isNotBlank(path)) {
                paths.add(path);
            }
        }
        // OK
        return paths;
    }

}

