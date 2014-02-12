package net.ontrack.extension.svn;

import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gui/extension/svn/configuration")
public class SVNConfigurationController extends AbstractGUIController implements TopActionExtension {

    @Autowired
    public SVNConfigurationController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    public String getExtension() {
        return SubversionExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "configuration";
    }

    @Override
    public AuthorizationPolicy getAuthorizationPolicy() {
        return AuthorizationPolicy.forGlobal(GlobalFunction.SETTINGS);
    }

    @Override
    public String getPath() {
        return "gui/extension/svn/configuration";
    }

    @Override
    public String getTitleKey() {
        return "subversion.configuration";
    }
}
