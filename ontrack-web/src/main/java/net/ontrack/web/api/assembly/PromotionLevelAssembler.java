package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.PromotionLevelSummary;
import net.ontrack.web.api.model.PromotionLevelResource;

public interface PromotionLevelAssembler {

    Function<PromotionLevelSummary, PromotionLevelResource> summary();

}
