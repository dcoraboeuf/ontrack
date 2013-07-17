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
@RequestMapping("/rest/branch")
public class BranchResourceController extends AbstractResourceController {

    private final ManagementService managementService;
    private final BranchResourceAssembler branchResourceAssembler;

    @Autowired
    public BranchResourceController(Strings strings, ManagementService managementService, BranchResourceAssembler branchResourceAssembler) {
        super(strings);
        this.managementService = managementService;
        this.branchResourceAssembler = branchResourceAssembler;
    }

    @RequestMapping(value = "/{id:[\\d+]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchResource branchGet(@PathVariable int id) {
        return branchResourceAssembler.toResource(managementService.getBranch(id));
    }

}
