package net.ontrack.extension.git.model;

import lombok.Data;
import net.ontrack.core.support.MessageAnnotation;
import net.ontrack.core.tree.Node;
import net.ontrack.extension.git.client.GitCommit;

@Data
public class GitUICommit {

    private final GitCommit commit;
    private final Node<MessageAnnotation> annotatedMessage;
    private final String link;
    private final String elapsedTime;
    private final String formattedTime;

}
