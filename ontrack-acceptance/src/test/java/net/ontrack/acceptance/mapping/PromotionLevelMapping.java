package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.PromotionLevelSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.When;

public class PromotionLevelMapping {

    @Steps
    private PromotionLevelSteps promotionLevelSteps;

    @When("I am on the promotion level page for $project/$branch/$promotionLevel")
    public void promotion_level_page(String project, String branch, String promotionLevel) {
        promotionLevelSteps.open_promotion_level(project, branch, promotionLevel);
    }

}
