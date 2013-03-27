package net.ontrack.extension.svnexplorer;

import net.ontrack.extension.api.action.ActionExtension;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/gui/extension/svnexplorer/changelog")
public class ChangeLogActionController implements ActionExtension {

    @Override
    public String getExtension() {
        return SVNExplorerExtension.EXTENSION;
    }

    @Override
    public String getName() {
        return "changelog";
    }

    /**
     * Allowed to everybody.
     */
    @Override
    public String getRole() {
        return null;
    }

    @Override
    public String getPath() {
        return "gui/extension/svnexplorer/changelog";
    }

    @Override
    public String getTitleKey() {
        return "svnexplorer.changelog";
    }
}
