package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class FilteredValidationStamps {

    private final BranchSummary branch;
    private final List<FilteredValidationStamp> list;

}
