package net.ontrack.web.api.controller;

import com.google.common.collect.Lists;
import net.ontrack.core.support.EntityNameNotFoundException;
import net.ontrack.service.ManagementService;
import net.ontrack.web.api.model.ProjectResource;
import net.ontrack.web.support.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/project")
public class ProjectController extends APIController {

    private final EntityConverter entityConverter;
    private final ManagementService managementService;

    @Autowired
    public ProjectController(EntityConverter entityConverter, ManagementService managementService) {
        this.entityConverter = entityConverter;
        this.managementService = managementService;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<ProjectResource> getProjectList() {
        return Lists.transform(
                managementService.getProjectList(),
                ProjectResource.stubFn
        );
    }

    @RequestMapping(value = "/{name:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<ProjectResource> getProject(@PathVariable String name) {
        try {
            return new ResponseEntity<>(
                    ProjectResource.resourceFn
                            .apply(
                                    managementService.getProject(
                                            entityConverter.getProjectId(name)
                                    )
                            ),
                    HttpStatus.OK
            );
        } catch (EntityNameNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


}
