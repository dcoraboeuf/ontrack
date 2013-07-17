package net.ontrack.web.hateoas;

import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

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

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ProjectResource projectGet(@PathVariable int id) {
        return projectResourceAssembler.toResource(managementService.getProject(id));
    }

    @RequestMapping(value = "/{id}/branch", method = RequestMethod.GET)
    public
    @ResponseBody
    List<BranchResource> projectBranchList(@PathVariable int id) {
        return branchResourceAssembler.toResources(managementService.getBranchList(id));
    }

}
