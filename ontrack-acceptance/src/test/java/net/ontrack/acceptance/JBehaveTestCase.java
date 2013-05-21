package net.ontrack.acceptance;

import net.thucydides.jbehave.ThucydidesJUnitStories;
import org.apache.commons.lang3.StringUtils;

public class JBehaveTestCase extends ThucydidesJUnitStories {

    public JBehaveTestCase() {
        super();
        // Story pattern?
        String storyNames = System.getProperty("story.name");
        if (StringUtils.isNotBlank(storyNames)) {
            findStoriesCalled(storyNames);
        }
    }
}
