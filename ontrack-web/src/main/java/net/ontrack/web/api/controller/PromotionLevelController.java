package net.ontrack.web.api.controller;

import net.ontrack.service.ManagementService;
import net.ontrack.web.api.assembly.PromotionLevelAssembler;
import net.ontrack.web.api.model.PromotionLevelResource;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.ontrack.web.support.WebUtils;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Controller
@RequestMapping("/api")
public class PromotionLevelController extends APIController {

    private final EntityConverter entityConverter;
    private final ManagementService managementService;
    private final PromotionLevelAssembler promotionLevelAssembler;
    private final byte[] defaultPromotionLevelImage;

    @Autowired
    public PromotionLevelController(ErrorHandler errorHandler, Strings strings, EntityConverter entityConverter, ManagementService managementService, PromotionLevelAssembler promotionLevelAssembler) {
        super(errorHandler, strings);
        this.entityConverter = entityConverter;
        this.managementService = managementService;
        this.promotionLevelAssembler = promotionLevelAssembler;
        // Reads the default images
        defaultPromotionLevelImage = WebUtils.readBytes("/default_promotion_level.png");
    }

    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion-level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PromotionLevelResource getPromotionLevel(
            @PathVariable String project,
            @PathVariable String branch,
            @PathVariable String promotionLevel
    ) {
        return promotionLevelAssembler.summary().apply(
                managementService.getPromotionLevel(
                        entityConverter.getPromotionLevelId(project, branch, promotionLevel)
                )
        );
    }

    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}/branch/{branch:[A-Za-z0-9_\\.\\-]+}/promotion-level/{promotionLevel:[A-Za-z0-9_\\.\\-]+}/image")
    public ResponseEntity<byte[]> getPromotionLevelImage(
            @PathVariable String project,
            @PathVariable String branch,
            @PathVariable String promotionLevel
    ) {
        return image(
                managementService.imagePromotionLevel(
                        entityConverter.getPromotionLevelId(project, branch, promotionLevel)
                ),
                defaultPromotionLevelImage
        );
    }

}
