package net.ontrack.web.support.fm;

import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.ExtensionManager;
import net.ontrack.extension.api.action.ActionExtension;
import net.sf.jstring.Strings;

import java.util.Collection;

public class FnExtensionTopLevelActions extends AbstractFnExtensionActions {

    public FnExtensionTopLevelActions(Strings strings, ExtensionManager extensionManager, SecurityUtils securityUtils) {
        super(strings, extensionManager, securityUtils);
    }

    @Override
    protected Collection<? extends ActionExtension> getActions(ExtensionManager extensionManager) {
        return extensionManager.getTopLevelActions();
    }
}
