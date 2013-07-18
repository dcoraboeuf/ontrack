package net.ontrack.web.hateoas;

import com.google.common.base.Function;
import net.ontrack.core.model.ValidationStampSummary;
import net.ontrack.service.ManagementService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Controller
@RequestMapping("/rest/validation-stamp")
public class ValidationStampResourceController extends AbstractResourceController {

    public static final Function<ValidationStampSummary, ValidationStampResource> validationStampStubFn = new Function<ValidationStampSummary, ValidationStampResource>() {
        @Override
        public ValidationStampResource apply(ValidationStampSummary o) {
            return new ValidationStampResource(o)
                    .withLink(linkTo(methodOn(ValidationStampResourceController.class).validationStampGet(o.getId())).withSelfRel());
        }
    };
    public static final Function<ValidationStampSummary, ValidationStampResource> validationStampFn = new Function<ValidationStampSummary, ValidationStampResource>() {
        @Override
        public ValidationStampResource apply(ValidationStampSummary o) {
            return validationStampStubFn.apply(o);
            // TODO Account resource link
        }
    };
    private final ManagementService managementService;

    @Autowired
    public ValidationStampResourceController(Strings strings, ManagementService managementService) {
        super(strings);
        this.managementService = managementService;
    }

    @RequestMapping(value = "/{id:[\\d+]+}", method = RequestMethod.GET)
    public
    @ResponseBody
    ValidationStampResource validationStampGet(@PathVariable int id) {
        return validationStampFn.apply(managementService.getValidationStamp(id));
    }

}
