package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.core.tree.Node;
import net.ontrack.core.tree.support.Markup;
import net.ontrack.extension.git.client.GitCommit;

@Data
public class GitUICommit {

    private final GitCommit commit;
    private final Node<Markup> annotatedMessage;
    private final String link;
    private final String elapsedTime;
    private final String formattedTime;

}
