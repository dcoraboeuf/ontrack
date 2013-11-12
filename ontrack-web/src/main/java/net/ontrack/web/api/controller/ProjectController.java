package net.ontrack.web.api.controller;

import com.google.common.collect.Lists;
import net.ontrack.service.ManagementService;
import net.ontrack.web.api.model.ProjectResource;
import net.ontrack.web.support.EntityConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

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


}
