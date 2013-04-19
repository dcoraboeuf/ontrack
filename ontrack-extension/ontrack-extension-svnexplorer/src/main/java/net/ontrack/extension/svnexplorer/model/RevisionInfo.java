package net.ontrack.extension.svnexplorer.model;

import lombok.Data;
import net.ontrack.core.model.BuildSummary;

import java.util.Collection;

@Data
public class RevisionInfo {

    private final ChangeLogRevision changeLogRevision;
    private final Collection<BuildSummary> builds;

}
