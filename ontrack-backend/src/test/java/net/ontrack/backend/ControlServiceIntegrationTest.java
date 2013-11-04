package net.ontrack.backend;

import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.core.support.MapBuilder;
import net.ontrack.service.ControlService;
import net.ontrack.service.EventService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;
import java.util.concurrent.Callable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ControlServiceIntegrationTest extends AbstractBackendTest {

    @Autowired
    private EventService eventService;
    @Autowired
    private ControlService controlService;
    @Autowired
    private SecurityUtils securityUtils;

    @Test
    public void createBuild() throws Exception {
        // Prerequisites
        final BranchSummary branch = doCreateBranch();
        // New account
        Account account = doCreateAccount();
        // As account
        BuildSummary build = asAccount(account).call(new Callable<BuildSummary>() {
            @Override
            public BuildSummary call() throws Exception {
                return securityUtils.asAdmin(new Callable<BuildSummary>() {
                    @Override
                    public BuildSummary call() throws Exception {
                        return controlService.createBuild(branch.getId(), new BuildCreationForm("1", "Build 1", PropertiesCreationForm.create()));
                    }
                });
            }
        });
        // Checks
        assertNotNull(build);
        assertEquals("1", build.getName());
        // Signature must match the initial account name, not the admin
        DatedSignature s = eventService.getDatedSignature(
                Locale.ENGLISH,
                EventType.BUILD_CREATED,
                MapBuilder.of(Entity.PROJECT, branch.getProject().getId())
                        .with(Entity.BRANCH, branch.getId())
                        .with(Entity.BUILD, build.getId())
                        .get()
        );
        assertEquals(account.getId(), s.getSignature().getId().intValue());
        assertEquals(account.getFullName(), s.getSignature().getName());
    }

}
