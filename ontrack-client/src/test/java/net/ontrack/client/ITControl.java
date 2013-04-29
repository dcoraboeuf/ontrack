package net.ontrack.client;

import net.ontrack.core.model.*;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITControl extends AbstractEnv {

    @Test
    public void createBuild() {
        // Prerequisites
        final BranchSummary branch = doCreateBranch();
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

    @Test
    public void createValidationRun() {
        // Prerequisites
        final ValidationStampSummary validationStamp = doCreateValidationStamp();
        final BuildSummary build = doCreateBuild(validationStamp.getBranch());
        // Creates a validation run
        ValidationRunSummary validationRun = asAdmin(new ControlCall<ValidationRunSummary>() {
            @Override
            public ValidationRunSummary call(ControlUIClient client) {
                return client.createValidationRun(
                        build.getBranch().getProject().getName(),
                        build.getBranch().getName(),
                        build.getName(),
                        validationStamp.getName(),
                        new ValidationRunCreationForm(
                                Status.PASSED,
                                "Test validation run",
                                PropertiesCreationForm.create()
                        )
                );
            }
        });
        // Checks
        assertNotNull(validationRun);
        assertEquals(build.getBranch().getProject().getName(), validationRun.getBuild().getBranch().getProject().getName());
        assertEquals(build.getBranch().getName(), validationRun.getBuild().getBranch().getName());
        assertEquals(build.getName(), validationRun.getBuild().getName());
        assertEquals(validationStamp.getName(), validationRun.getValidationStamp().getName());
        assertEquals(1, validationRun.getRunOrder());
        assertEquals(Status.PASSED, validationRun.getValidationRunStatus().getStatus());
    }
}
