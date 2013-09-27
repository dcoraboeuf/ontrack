package net.ontrack.backend.export;

import lombok.Data;
import net.ontrack.backend.dao.model.TBranch;
import net.ontrack.backend.dao.model.TProject;

import java.util.Collection;

@Data
public class TExport {

    private final TProject project;
    private final Collection<TBranch> branches;

}
