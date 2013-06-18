package net.ontrack.extension.git.client.impl;

import net.ontrack.extension.git.client.GitClient;
import net.ontrack.extension.git.client.GitClientFactory;
import net.ontrack.extension.git.client.GitCommit;
import net.ontrack.extension.git.model.GitConfiguration;
import net.ontrack.service.EnvironmentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.File;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

public class RealGitClientTest {

    private GitClientFactory gitClientFactory;

    @Before
    public void before() {
        EnvironmentService environmentService = mock(EnvironmentService.class);
        when(environmentService.getWorkingDir(anyString(), anyString())).thenAnswer(new Answer<File>() {
            @Override
            public File answer(InvocationOnMock invocation) throws Throwable {
                String context = (String) invocation.getArguments()[0];
                String path = (String) invocation.getArguments()[1];
                return new File(String.format("target/work/%s/%s", context, path));
            }
        });
        GitRepositoryManager repositoryManager = new DefaultGitRepositoryManager(environmentService);
        gitClientFactory = new DefaultGitClientFactory(repositoryManager);
    }

    @Test
    public void log() {
        GitClient client = gitClientFactory.getClient(new GitConfiguration(
                "https://github.com/dcoraboeuf/ontrack.git",
                "master",
                "ontrack-*"
        ));
        GitCommit root = client.log("ontrack-1.19", "ontrack-1.21");
        assertNotNull(root);
    }

}
