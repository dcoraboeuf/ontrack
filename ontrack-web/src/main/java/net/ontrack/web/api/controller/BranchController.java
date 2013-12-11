package net.ontrack.web.api.controller;

import net.ontrack.core.support.EntityNameNotFoundException;
import net.ontrack.service.ManagementService;
import net.ontrack.web.api.assembly.BranchAssembler;
import net.ontrack.web.api.model.BranchResource;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/api")
public class BranchController extends APIController {

    private final BranchAssembler branchAssembler;
    private final ManagementService managementService;
    private final EntityConverter entityConverter;

    @Autowired
    public BranchController(ErrorHandler errorHandler, Strings strings, BranchAssembler branchAssembler, ManagementService managementService, EntityConverter entityConverter) {
        super(errorHandler, strings);
        this.branchAssembler = branchAssembler;
        this.managementService = managementService;
        this.entityConverter = entityConverter;
    }

    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<BranchResource> getBranch(@PathVariable String project, @PathVariable String branch) {
        try {
            return new ResponseEntity<>(
                    branchAssembler.detail()
                            .apply(
                                    managementService.getBranch(
                                            entityConverter.getBranchId(project, branch)
                                    )
                            ),
                    HttpStatus.OK
            );
        } catch (EntityNameNotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
