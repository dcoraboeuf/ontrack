package net.ontrack.extension.git.model;

import com.google.common.base.Function;
import lombok.Data;
import net.ontrack.extension.git.client.GitCommit;

@Data
public class GitUICommit {

    public static final Function<GitUICommit, GitCommit> getCommitFn = new Function<GitUICommit, GitCommit>() {
        @Override
        public GitCommit apply(GitUICommit ui) {
            return ui.getCommit();
        }
    };

    private final GitCommit commit;
    private final String annotatedMessage;
    private final String link;
    private final String elapsedTime;
    private final String formattedTime;

}
