package net.ontrack.web.api.model;

import com.google.common.base.Function;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.ontrack.core.model.PromotionLevelSummary;
import net.ontrack.web.api.controller.PromotionLevelController;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Data
@EqualsAndHashCode(callSuper = false)
public class PromotionLevelResource extends AbstractResource<PromotionLevelResource> {

    public static Function<PromotionLevelSummary, PromotionLevelResource> summary = new Function<PromotionLevelSummary, PromotionLevelResource>() {
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
    private final int id;
    private final String project;
    private final String branch;
    private final int levelNb;
    private final String name;
    private final String description;
    private final boolean autoPromote;

}
