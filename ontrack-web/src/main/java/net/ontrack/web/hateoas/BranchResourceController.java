package net.ontrack.web.hateoas;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.service.ManagementService;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.validation.constraints.NotNull;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/rest/branch")
public class BranchResourceController extends AbstractResourceController {

    public static final Function<BranchSummary, BranchResource> branchStubFn = new Function<BranchSummary, BranchResource>() {
        @Override
        public BranchResource apply(BranchSummary o) {
            return new BranchResource(o)
                    .withLink(linkTo(methodOn(BranchResourceController.class).branchGet(o.getId())).withSelfRel());
        }
    };
    public static final Function<BranchSummary, BranchResource> branchFn = new Function<BranchSummary, BranchResource>() {

        @Override
        public BranchResource apply(@NotNull BranchSummary o) {
            //noinspection ConstantConditions
            return branchStubFn.apply(o)
                    .withLink(linkTo(methodOn(ProjectResourceController.class).projectGet(o.getProject().getId())).withRel("project"))
                    .withLink(linkTo(methodOn(BranchResourceController.class).branchValidationStampList(o.getId())).withRel("validationStamps"));
        }
    };
    private final ManagementService managementService;

    @Autowired
    public BranchResourceController(ErrorHandler errorHandler, Strings strings, ManagementService managementService) {
        super(errorHandler, strings);
        this.managementService = managementService;
    }

    @RequestMapping(value = "/{id:[\\d+]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    BranchResource branchGet(@PathVariable int id) {
        return branchFn.apply(managementService.getBranch(id));
    }

    @RequestMapping(value = "/{id:[\\d+]+}/validation-stamp", method = RequestMethod.GET)
    public
    @ResponseBody
    List<ValidationStampResource> branchValidationStampList(@PathVariable int id) {
        return Lists.transform(
                managementService.getValidationStampList(id),
                ValidationStampResourceController.validationStampStubFn
        );
    }

}
