package net.ontrack.web.support.fm;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.model.NamedLink;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.ui.ManageUI;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.action.EntityActionExtension;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.context.i18n.LocaleContextHolder;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class FnExtensionProjectActions implements TemplateMethodModel {

    private final Strings strings;
    private final ExtensionManager extensionManager;
    private final SecurityUtils securityUtils;
    private final ManageUI manageUI;

    public FnExtensionProjectActions(Strings strings, ExtensionManager extensionManager, SecurityUtils securityUtils, ManageUI manageUI) {
        this.strings = strings;
        this.extensionManager = extensionManager;
        this.securityUtils = securityUtils;
        this.manageUI = manageUI;
    }

    @Override
    public Collection<NamedLink> exec(List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 1, "One argument (project name) is needed");
        // Project name
        String projectName = (String) list.get(0);
        // Gets the list of entity actions for projects
        Collection<EntityActionExtension<ProjectSummary>> projectActions = extensionManager.getProjectActions();
        // If at least one action
        if (!projectActions.isEmpty()) {
            // Gets the project summary
            final ProjectSummary projectSummary = manageUI.getProject(projectName);
            // Filters on role
            projectActions = Collections2.filter(
                    projectActions,
                    new Predicate<EntityActionExtension<ProjectSummary>>() {
                        @Override
                        public boolean apply(EntityActionExtension<ProjectSummary> action) {
                            if (action.isEnabled(projectSummary)) {
                                String actionRole = action.getRole(projectSummary);
                                return actionRole == null || securityUtils.hasRole(actionRole);
                            } else {
                                return false;
                            }
                        }
                    }
            );
            // Gets the locale from the context
            final Locale locale = LocaleContextHolder.getLocale();
            // Converts the list of actions to links
            return Collections2.transform(
                    projectActions,
                    new Function<EntityActionExtension<ProjectSummary>, NamedLink>() {
                        @Override
                        public NamedLink apply(EntityActionExtension<ProjectSummary> action) {
                            NamedLink link = new NamedLink(
                                    action.getPath(projectSummary),
                                    action.getTitle(projectSummary).getLocalizedMessage(strings, locale)
                            );
                            // Icon
                            String icon = action.getIcon(projectSummary);
                            if (StringUtils.isNotBlank(icon)) {
                                link = link.withIcon(icon);
                            }
                            // CSS
                            String css = action.getCss(projectSummary);
                            if (StringUtils.isNotBlank(css)) {
                                link = link.withCss(css);
                            }
                            // OK
                            return link;
                        }
                    }
            );
        }
        // Nothing to do
        else {
            return Collections.emptyList();
        }
    }
}
