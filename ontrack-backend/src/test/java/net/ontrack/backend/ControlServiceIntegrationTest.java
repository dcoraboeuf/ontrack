package net.ontrack.backend;

import net.ontrack.core.model.BranchSummary;
import net.ontrack.core.model.BuildCreationForm;
import net.ontrack.core.model.BuildSummary;
import net.ontrack.core.model.PropertiesCreationForm;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.service.ControlService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ControlServiceIntegrationTest extends AbstractBackendTest {

    @Autowired
    private ControlService controlService;
    @Autowired
    private SecurityUtils securityUtils;

    @Test
    public void createBuild() throws Exception {
        // Prerequisites
        final BranchSummary branch = doCreateBranch();
        // Creates a build
        BuildSummary build = securityUtils.asAdmin(new Callable<BuildSummary>() {
            @Override
            public BuildSummary call() throws Exception {
                return controlService.createBuild(branch.getId(), new BuildCreationForm("1", "Build 1", PropertiesCreationForm.create()));
            }
        });
        // Checks
        assertNotNull(build);
        assertEquals("1", build.getName());
    }

}
