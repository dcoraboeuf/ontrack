package net.ontrack.web.hateoas;

import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectUpdateForm;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/rest/project")
public class ProjectResourceController extends AbstractResourceController {

    private final ManagementService managementService;
    private final ProjectResourceAssembler projectResourceAssembler;
    private final BranchResourceAssembler branchResourceAssembler;

    @Autowired
    public ProjectResourceController(Strings strings, ManagementService managementService, ProjectResourceAssembler projectResourceAssembler, BranchResourceAssembler branchResourceAssembler) {
        super(strings);
        this.managementService = managementService;
        this.projectResourceAssembler = projectResourceAssembler;
        this.branchResourceAssembler = branchResourceAssembler;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ProjectResource> projectList() {
        return projectResourceAssembler.toResources(managementService.getProjectList());
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectResource projectCreate(@RequestBody ProjectCreationForm form) {
        return projectResourceAssembler.toResource(managementService.createProject(form));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ProjectResource projectGet(@PathVariable int id) {
        return projectResourceAssembler.toResource(managementService.getProject(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectResource projectUpdate(@PathVariable int id, @RequestBody ProjectUpdateForm form) {
        return projectResourceAssembler.toResource(managementService.updateProject(id, form));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public
    @ResponseBody
    Ack projectDelete(@PathVariable int id) {
        return managementService.deleteProject(id);
    }

    @RequestMapping(value = "/{id}/branch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BranchResource> projectBranchList(@PathVariable int id) {
        return branchResourceAssembler.toResources(managementService.getBranchList(id));
    }

}
