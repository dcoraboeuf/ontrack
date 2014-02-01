package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

/**
 * Definition of a custom dashboard
 */
@Data
public class DashboardConfig {

    private final int id;
    private final String name;
    private final List<BranchSummary> branches;

}
