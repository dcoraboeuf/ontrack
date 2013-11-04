package net.ontrack.web.support.fm;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.Entity;
import net.ontrack.core.security.AuthorizationUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.action.ActionExtension;
import net.sf.jstring.Strings;
import org.apache.commons.lang3.Validate;

import java.util.Collection;
import java.util.List;

public class FnExtensionDiffActions extends AbstractFnExtensionActions {

    public FnExtensionDiffActions(Strings strings, ExtensionManager extensionManager, AuthorizationUtils authorizationUtils) {
        super(strings, extensionManager, authorizationUtils);
    }

    @Override
    protected Collection<? extends ActionExtension> getActions(ExtensionManager extensionManager, List<String> arguments) {
        // Checks
        Validate.notNull(arguments, "List of arguments is required");
        Validate.isTrue(arguments.size() == 1, "One argument (branch id) is needed");
        // Branch ID
        final int branchId = Integer.parseInt(arguments.get(0), 10);
        // List of all diff actions
        Collection<? extends ActionExtension> actions = extensionManager.getDiffActions();
        // Filter on configuration
        return Collections2.filter(actions, new Predicate<ActionExtension>() {
            @Override
            public boolean apply(ActionExtension action) {
                return action.isApplicable(Entity.BRANCH, branchId);
            }
        });
    }
}
