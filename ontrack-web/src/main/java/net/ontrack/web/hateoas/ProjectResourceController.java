package net.ontrack.web.hateoas;

import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.ui.ManageUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rest/project")
public class ProjectResourceController {

    private final ManageUI manageUI;
    private final ProjectResourceAssembler projectResourceAssembler;

    @Autowired
    public ProjectResourceController(ManageUI manageUI, ProjectResourceAssembler projectResourceAssembler) {
        this.manageUI = manageUI;
        this.projectResourceAssembler = projectResourceAssembler;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public void projectList() {

    }

    @RequestMapping(value = "/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ProjectResource projectGet(@PathVariable String name) {
        ProjectSummary p = manageUI.getProject(name);
        return projectResourceAssembler.toResource(p);
    }

}
