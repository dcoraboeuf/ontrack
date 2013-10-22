package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.util.Collection;
import java.util.Collections;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ImportResult {

    private final Ack finished;
    private final Collection<ProjectSummary> importedProjects;
    private final Collection<String> rejectedProjects;

    public static final ImportResult NOT_FINISHED = new ImportResult(
            Ack.NOK,
            Collections.<ProjectSummary>emptySet(),
            Collections.<String>emptySet()
    );

    public ImportResult(Collection<ProjectSummary> importedProjects, Collection<String> rejectedProjects) {
        this(Ack.OK, importedProjects, rejectedProjects);
    }

    @JsonIgnore
    public boolean isFinished() {
        return finished.isSuccess();
    }
}
