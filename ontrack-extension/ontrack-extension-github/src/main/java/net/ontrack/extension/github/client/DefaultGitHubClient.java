package net.ontrack.extension.github.client;

import net.ontrack.extension.github.model.GitHubIssue;
import org.springframework.stereotype.Component;

@Component
public class DefaultGitHubClient implements GitHubClient {

    @Override
    public GitHubIssue getIssue(String project, int id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

}
