package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.PromotionLevelSummary;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.web.api.controller.PromotionLevelController;
import net.ontrack.web.api.model.PromotionLevelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class PromotionLevelAssemblerImpl extends AbstractAssembler implements PromotionLevelAssembler {

    @Autowired
    public PromotionLevelAssemblerImpl(SecurityUtils securityUtils) {
        super(securityUtils);
    }

    @Override
    public Function<PromotionLevelSummary, PromotionLevelResource> summary() {
        return new Function<PromotionLevelSummary, PromotionLevelResource>() {
            @Override
            public PromotionLevelResource apply(PromotionLevelSummary o) {
                return new PromotionLevelResource(
                        o.getId(),
                        o.getBranch().getProject().getName(),
                        o.getBranch().getName(),
                        o.getLevelNb(),
                        o.getName(),
                        o.getDescription(),
                        o.isAutoPromote()
                )
                        // Self link
                        .withLink(linkTo(methodOn(PromotionLevelController.class).getPromotionLevel(
                                o.getBranch().getProject().getName(),
                                o.getBranch().getName(),
                                o.getName()
                        )).withSelfRel())
                                // View
                        .withView(
                                "/project/%s/branch/%s/promotion-level/%s",
                                o.getBranch().getProject().getName(),
                                o.getBranch().getName(),
                                o.getName())
                                // Image
                        .withLink(linkTo(methodOn(PromotionLevelController.class).getPromotionLevelImage(
                                o.getBranch().getProject().getName(),
                                o.getBranch().getName(),
                                o.getName()
                        )).withRel("image"))
                        ;
            }
        };
    }
}
