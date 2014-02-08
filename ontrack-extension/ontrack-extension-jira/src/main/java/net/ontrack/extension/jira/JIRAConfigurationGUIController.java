package net.ontrack.extension.jira;

import net.ontrack.core.security.AuthorizationPolicy;
import net.ontrack.core.security.GlobalFunction;
import net.ontrack.extension.api.action.TopActionExtension;
import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Link to the page that allows the user to configure the JIRA extensions.
 */
@Controller
@RequestMapping("/gui/extension/jira/configuration")
public class JIRAConfigurationGUIController extends AbstractGUIController implements TopActionExtension {

    @Autowired
    public JIRAConfigurationGUIController(ErrorHandler errorHandler) {
        super(errorHandler);
    }

    @Override
    public String getExtension() {
        return JIRAExtension.EXTENSION;
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
        return "gui/extension/jira/configuration";
    }

    @Override
    public String getTitleKey() {
        return "jira.configuration";
    }
}
