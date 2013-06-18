package net.ontrack.extension.git.service;

import net.ontrack.core.model.*;
import net.ontrack.core.security.SecurityUtils;
import net.ontrack.extension.api.property.PropertiesService;
import net.ontrack.extension.git.GitBranchProperty;
import net.ontrack.extension.git.GitExtension;
import net.ontrack.extension.git.GitRemoteProperty;
import net.ontrack.extension.git.GitTagProperty;
import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitClientFactory;
import net.ontrack.extension.git.client.GitTag;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.extension.git.model.GitImportBuildsForm;
import net.ontrack.service.ControlService;
import net.ontrack.service.ManagementService;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

public class DefaultGitServiceTest {

    private DefaultGitService service;
    private SecurityUtils securityUtils;
    private PropertiesService propertiesService;
    private ManagementService managementService;
    private ControlService controlService;
    private GitClientFactory gitClientFactory;

    @Before
    public void before() {
        securityUtils = mock(SecurityUtils.class);
        propertiesService = mock(PropertiesService.class);
        managementService = mock(ManagementService.class);
        controlService = mock(ControlService.class);
        gitClientFactory = mock(GitClientFactory.class);
        service = new DefaultGitService(
                securityUtils,
                propertiesService,
                managementService,
                controlService,
                gitClientFactory
        );
    }

    @Test
    public void doImportBuilds() {
        // Prerequisites

        ProjectSummary project = new ProjectSummary(1, "P1", "Project 1");
        BranchSummary branch = new BranchSummary(1, "B1", "Branch 1", project);
        when(managementService.getBranch(1)).thenReturn(branch);


        when(propertiesService.getPropertyValue(Entity.PROJECT, 1, GitExtension.EXTENSION, GitRemoteProperty.NAME)).thenReturn("git:ontrack");
        when(propertiesService.getPropertyValue(Entity.BRANCH, 1, GitExtension.EXTENSION, GitBranchProperty.NAME)).thenReturn("master");
        when(propertiesService.getPropertyValue(Entity.BRANCH, 1, GitExtension.EXTENSION, GitTagProperty.NAME)).thenReturn("ontrack-*");

        GitClient gitClient = mock(GitClient.class);
        when(gitClient.getTags()).thenReturn(Arrays.asList(
                new GitTag("ontrack-0.6", new DateTime(2013, 6, 18, 0, 6)),
                new GitTag("ontrack-0.7", new DateTime(2013, 6, 18, 0, 7)),
                new GitTag("ontrack-0.8", new DateTime(2013, 6, 18, 0, 8)),
                new GitTag("ontrack-0.9", new DateTime(2013, 6, 18, 0, 9)),
                new GitTag("ontrack-1.9", new DateTime(2013, 6, 18, 1, 9)),
                new GitTag("ontrack-1.10", new DateTime(2013, 6, 18, 1, 10)),
                new GitTag("ontrack-1.11", new DateTime(2013, 6, 18, 1, 11)),
                new GitTag("ontrack-1.12", new DateTime(2013, 6, 18, 1, 12))
        ));
        when(gitClientFactory.getClient(any(GitConfiguration.class))).thenReturn(gitClient);

        // Input
        GitImportBuildsForm form = new GitImportBuildsForm();
        form.setOverride(true);
        form.setTagPattern("ontrack-(1\\.\\d+)");
        // Call
        service.doImportBuilds(1, form);
        // Checks
        verify(controlService).createBuild(1, new BuildCreationForm("1.9", "Imported from Git tag ontrack-1.9", PropertiesCreationForm.create()));
        verify(controlService).createBuild(1, new BuildCreationForm("1.10", "Imported from Git tag ontrack-1.10", PropertiesCreationForm.create()));
        verify(controlService).createBuild(1, new BuildCreationForm("1.11", "Imported from Git tag ontrack-1.11", PropertiesCreationForm.create()));
        verify(controlService).createBuild(1, new BuildCreationForm("1.12", "Imported from Git tag ontrack-1.12", PropertiesCreationForm.create()));
    }

}
