package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class Dashboard {

    private final String title;
    private final List<BranchSummary> branches;

}
