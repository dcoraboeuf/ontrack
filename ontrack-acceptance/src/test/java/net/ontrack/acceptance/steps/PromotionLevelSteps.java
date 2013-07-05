package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.pages.PromotionLevelPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;

import static org.junit.Assert.assertTrue;

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
        any(
                promotionLevelPage.getValidationStamps(),
                validationStamp,
                "Could not find validation stamp " + validationStamp
        );
    }

    @Step
    public void promotion_level_is_auto_promoted() {
        assertTrue(
                "The promotion level is not indicated as promoted",
                promotionLevelPage.isAutoPromoted()
        );
    }
}
