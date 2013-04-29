package net.ontrack.client;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.PropertiesCreationForm;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITControl extends AbstractEnv {

    @Test
    public void createBuild() {
        // Prerequisites
        final BranchSummary branch = createBranch();
        // Creates a build
        final String buildName = uid("BLD");
        BuildSummary build = asAdmin(new ControlCall<BuildSummary>() {
            @Override
            public BuildSummary call(ControlUIClient client) {
                return client.createBuild(
                        branch.getProject().getName(),
                        branch.getName(),
                        new BuildCreationForm(
                                buildName,
                                "Test build",
                                PropertiesCreationForm.create()
                        )
                );
            }
        });
        // Checks
        assertNotNull(build);
        assertEquals(buildName, build.getName());
        assertEquals(branch.getName(), build.getBranch().getName());
        assertEquals(branch.getProject().getName(), build.getBranch().getProject().getName());
    }

}
