package net.ontrack.acceptance.steps;

import net.ontrack.acceptance.pages.PromotionLevelPage;
import net.thucydides.core.annotations.Step;
import net.thucydides.core.pages.Pages;
import net.thucydides.core.steps.ScenarioSteps;

public class PromotionLevelSteps extends ScenarioSteps {

    private final PromotionLevelPage promotionLevelPage;

    public PromotionLevelSteps(Pages pages) {
        super(pages);
        promotionLevelPage = pages.getPage(PromotionLevelPage.class);
    }

    @Step
    public void open_promotion_level(String project, String branch, String promotionLevel) {
        promotionLevelPage.open(project, branch, promotionLevel);
    }
}
