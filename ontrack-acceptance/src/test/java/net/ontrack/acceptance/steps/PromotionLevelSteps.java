package net.ontrack.acceptance.steps;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import net.ontrack.acceptance.pages.PromotionLevelPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import org.apache.commons.lang3.StringUtils;

import static org.junit.Assert.assertEquals;

public class PromotionLevelSteps extends AbstractSteps {

    private final PromotionLevelPage promotionLevelPage;

    public PromotionLevelSteps(Pages pages) {
        super(pages);
        promotionLevelPage = pages.getPage(PromotionLevelPage.class);
    }

    @Step
    public void open_promotion_level(String project, String branch, String promotionLevel) {
        promotionLevelPage.open(project, branch, promotionLevel);
    }

    @Step
    public void promotion_level_validation_stamp_presence_check(final String validationStamp) {
        String value = Iterables.find(
                promotionLevelPage.getValidationStamps(),
                new Predicate<String>() {
                    @Override
                    public boolean apply(String stamp) {
                        return StringUtils.equals(validationStamp, stamp);
                    }
                },
                null
        );
        assertEquals("Could not find validation stamp " + validationStamp, validationStamp, value);
    }
}
