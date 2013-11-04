package net.ontrack.web.support.fm;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.security.ProjectFunction;
import net.ontrack.core.security.SecurityUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class FnSecProject implements TemplateMethodModel {

    private final SecurityUtils securityUtils;

    @Autowired
    public FnSecProject(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List list)
            throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 2, "Two arguments are needed");
        // Project ID
        int project = Integer.parseInt((String) list.get(0), 10);
        // Function to validate
        ProjectFunction fn = ProjectFunction.valueOf((String) list.get(1));
        // Test
        return securityUtils.isGranted(fn, project);
    }

}
