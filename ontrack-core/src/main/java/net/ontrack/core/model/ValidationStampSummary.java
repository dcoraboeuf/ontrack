package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationStampSummary implements Comparable<ValidationStampSummary> {

    private final int id;
    private final String name;
    private final String description;
    private final BranchSummary branch;
    private final int orderNb;
    private final AccountSummary owner;

    @Override
    public int compareTo(ValidationStampSummary o) {
        return name.compareTo(o.name);
    }
}
