package net.ontrack.extension.svn;

import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gui/extension/svn/configuration")
public class SVNConfigurationController extends AbstractGUIController implements TopActionExtension {

    private final SecurityUtils securityUtils;

    @Autowired
    public SVNConfigurationController(ErrorHandler errorHandler, SecurityUtils securityUtils) {
        super(errorHandler);
        this.securityUtils = securityUtils;
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

    /**
     * Configuration page
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getPage() {
        securityUtils.checkGrant(GlobalFunction.SETTINGS);
        return "extension/svn/configuration";
    }
}
