package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class DecoratedBranch {

    private final BranchSummary summary;
    private final List<LocalizedDecoration> decorations;

}
