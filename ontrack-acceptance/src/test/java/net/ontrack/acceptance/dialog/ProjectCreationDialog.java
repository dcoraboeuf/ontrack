package net.ontrack.acceptance.dialog;

import org.openqa.selenium.WebDriver;

public class ProjectCreationDialog extends AbstractDialog {

    public ProjectCreationDialog(WebDriver driver) {
        super(driver);
    }

    @Override
    public void waitForLoad() {
        waitFor("#project-name");
    }

    public void submit() {
        $("#dialog-project-create button[type='submit']").click();
    }

    public void setNameAndDescription(String name, String description) {
        $("#project-name").typeAndTab(name);
        $("#project-description").type(description);
    }
}
