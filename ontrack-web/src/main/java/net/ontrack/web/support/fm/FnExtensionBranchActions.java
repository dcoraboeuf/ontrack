package net.ontrack.web.support.fm;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.NamedLink;
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

public class FnExtensionBranchActions implements TemplateMethodModel {

    private final Strings strings;
    private final ExtensionManager extensionManager;
    private final SecurityUtils securityUtils;
    private final ManageUI manageUI;

    public FnExtensionBranchActions(Strings strings, ExtensionManager extensionManager, SecurityUtils securityUtils, ManageUI manageUI) {
        this.strings = strings;
        this.extensionManager = extensionManager;
        this.securityUtils = securityUtils;
        this.manageUI = manageUI;
    }

    @Override
    public Collection<NamedLink> exec(List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.size() == 2, "Two arguments (project name, branch name) are needed");
        // Project & branch name
        String projectName = (String) list.get(0);
        String branchName = (String) list.get(1);
        // Gets the list of entity actions for branches
        Collection<EntityActionExtension<BranchSummary>> branchActions = extensionManager.getBranchActions();
        // If at least one action
        if (!branchActions.isEmpty()) {
            // Gets the branch summary
            final BranchSummary branchSummary = manageUI.getBranch(projectName, branchName);
            // Filters on role
            branchActions = Collections2.filter(
                    branchActions,
                    new Predicate<EntityActionExtension<BranchSummary>>() {
                        @Override
                        public boolean apply(EntityActionExtension<BranchSummary> action) {
                            if (action.isEnabled(branchSummary)) {
                                String actionRole = action.getRole(branchSummary);
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
                    branchActions,
                    new Function<EntityActionExtension<BranchSummary>, NamedLink>() {
                        @Override
                        public NamedLink apply(EntityActionExtension<BranchSummary> action) {
                            NamedLink link = new NamedLink(
                                    action.getPath(branchSummary),
                                    action.getTitle(branchSummary).getLocalizedMessage(strings, locale)
                            );
                            // Icon
                            String icon = action.getIcon(branchSummary);
                            if (StringUtils.isNotBlank(icon)) {
                                link = link.withIcon(icon);
                            }
                            // CSS
                            String css = action.getCss(branchSummary);
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
