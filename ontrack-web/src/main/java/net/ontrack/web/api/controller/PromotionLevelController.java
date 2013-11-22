package net.ontrack.web.api.controller;

import net.ontrack.service.ManagementService;
import net.ontrack.web.api.model.PromotionLevelResource;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
public class PromotionLevelController extends APIController {

    private final EntityConverter entityConverter;
    private final ManagementService managementService;

    @Autowired
    public PromotionLevelController(ErrorHandler errorHandler, Strings strings, EntityConverter entityConverter, ManagementService managementService) {
        super(errorHandler, strings);
        this.entityConverter = entityConverter;
        this.managementService = managementService;
    }

    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion-level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}")
    public ResponseEntity<PromotionLevelResource> getPromotionLevel(
            @PathVariable String project,
            @PathVariable String branch,
            @PathVariable String promotionLevel
    ) {
        return ok(
                PromotionLevelResource.summary.apply(
                        managementService.getPromotionLevel(
                                entityConverter.getPromotionLevelId(project, branch, promotionLevel)
                        )
                )
        );
    }

    @RequestMapping(
            value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion-level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/image",
            produces = MediaType.IMAGE_PNG_VALUE
    )
    public byte[] getPromotionLevelImage(
            @PathVariable String project,
            @PathVariable String branch,
            @PathVariable String promotionLevel
    ) {
        return managementService.imagePromotionLevel(
                entityConverter.getPromotionLevelId(project, branch, promotionLevel)
        );
    }

}
