package net.ontrack.extension.svn;

import net.ontrack.core.security.SecurityRoles;
import net.ontrack.extension.api.action.ActionExtension;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/gui/extension/subversion/indexation")
public class IndexationActionController implements ActionExtension {

    @Override
    public String getRole() {
        return SecurityRoles.ADMINISTRATOR;
    }

    @Override
    public String getPath() {
        return "gui/extension/subversion/indexation";
    }

    @Override
    public String getTitleKey() {
        return "subversion.indexation";
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public String getPage(Model model) {
        // OK
        return "extension/subversion/indexation";
    }
}
