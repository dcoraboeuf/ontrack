package net.ontrack.web.api.model;

import lombok.Data;
import net.ontrack.core.model.DatedSignature;

@Data
public class PromotionResource extends AbstractResource<PromotionResource> {

    private final PromotionLevelResource promotionLevel;
    private final BuildResource build;
    private final DatedSignature signature;

}
