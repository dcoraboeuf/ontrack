package net.ontrack.web.hateoas;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.Ack;
import net.ontrack.core.model.ProjectCreationForm;
import net.ontrack.core.model.ProjectSummary;
import net.ontrack.core.model.ProjectUpdateForm;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/rest/project")
public class ProjectResourceController extends AbstractResourceController {

    public static final Function<ProjectSummary, ProjectResource> projectStubFn = new Function<ProjectSummary, ProjectResource>() {
        @Override
        public ProjectResource apply(ProjectSummary o) {
            return new ProjectResource(o)
                    .withLink(linkTo(methodOn(ProjectResourceController.class).projectGet(o.getId())).withSelfRel());
        }
    };
    public static final Function<ProjectSummary, ProjectResource> projectFn = new Function<ProjectSummary, ProjectResource>() {

        @Override
        public ProjectResource apply(ProjectSummary o) {
            return projectStubFn.apply(o)
                    .withLink(linkTo(methodOn(ProjectResourceController.class).projectBranchList(o.getId())).withRel("branches"));
        }
    };
    private final ManagementService managementService;

    @Autowired
    public ProjectResourceController(Strings strings, ManagementService managementService) {
        super(strings);
        this.managementService = managementService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ProjectResource> projectList() {
        return Lists.transform(
                managementService.getProjectList(),
                projectStubFn
        );
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectResource projectCreate(@RequestBody ProjectCreationForm form) {
        return projectFn.apply(managementService.createProject(form));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public
    @ResponseBody
    ProjectResource projectGet(@PathVariable int id) {
        return projectFn.apply(managementService.getProject(id));
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.POST)
    public
    @ResponseBody
    ProjectResource projectUpdate(@PathVariable int id, @RequestBody ProjectUpdateForm form) {
        return projectFn.apply(managementService.updateProject(id, form));
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
        return Lists.transform(
                managementService.getBranchList(id),
                BranchResourceController.branchStubFn
        );
    }

}
