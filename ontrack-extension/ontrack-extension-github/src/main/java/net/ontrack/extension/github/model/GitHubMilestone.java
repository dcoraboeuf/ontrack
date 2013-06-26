package net.ontrack.extension.github.model;

import lombok.Data;

@Data
public class GitHubMilestone {

    private final String title;
    private final GitHubState state;

}
