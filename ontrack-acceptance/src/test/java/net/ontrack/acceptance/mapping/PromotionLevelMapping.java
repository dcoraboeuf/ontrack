package net.ontrack.acceptance.mapping;

import net.ontrack.acceptance.steps.PromotionLevelSteps;
import net.thucydides.core.annotations.Steps;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

public class PromotionLevelMapping {

    @Steps
    private PromotionLevelSteps promotionLevelSteps;

    @When("I am on the promotion level page for $project/$branch/$promotionLevel")
    public void promotion_level_page(String project, String branch, String promotionLevel) {
        promotionLevelSteps.open_promotion_level(project, branch, promotionLevel);
    }

    @Then("on the promotion level page, I see the validation stamp $validationStamp")
    public void promotion_level_validation_stamp_presence_check(String validationStamp) {
        promotionLevelSteps.promotion_level_validation_stamp_presence_check(validationStamp);
    }

    @Then("on the promotion level page, I see that the promotion level is autopromoted")
    public void promotion_level_is_auto_promoted() {
        promotionLevelSteps.promotion_level_is_auto_promoted();
    }

}
