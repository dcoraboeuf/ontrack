package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.controller.BranchController;
import net.ontrack.web.api.model.BranchResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class BranchAssemblerImpl extends AbstractAssembler implements BranchAssembler {

    @Autowired
    public BranchAssemblerImpl(SecurityUtils securityUtils) {
        super(securityUtils);
    }

    @Override
    public Function<BranchSummary, BranchResource> summary() {
        return new Function<BranchSummary, BranchResource>() {
            @Override
            public BranchResource apply(BranchSummary o) {
                return new BranchResource(
                        o.getId(),
                        o.getProject().getName(),
                        o.getName(),
                        o.getDescription()
                )
                        // Branch view
                        .withView("/project/%s/branch/%s", o.getProject().getName(), o.getName())
                        .withLink(linkTo(methodOn(BranchController.class).getBranch(o.getProject().getName(), o.getName())).withSelfRel())
                        ;
            }
        };
    }

    @Override
    public Function<BranchSummary, BranchResource> detail() {
        return new Function<BranchSummary, BranchResource>() {
            @Override
            public BranchResource apply(BranchSummary o) {
                return summary().apply(o);
            }
        };
    }
}
