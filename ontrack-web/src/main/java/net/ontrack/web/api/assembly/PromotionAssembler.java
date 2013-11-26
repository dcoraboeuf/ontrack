package net.ontrack.web.api.assembly;

import com.google.common.base.Function;
import net.ontrack.core.model.Promotion;
import net.ontrack.web.api.model.PromotionResource;

public interface PromotionAssembler {

    Function<Promotion, PromotionResource> summary();

}
