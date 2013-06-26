package net.ontrack.extension.github.client;

import net.ontrack.extension.github.model.GitHubIssue;

public interface GitHubClient {

    GitHubIssue getIssue(String project, int id);

}
