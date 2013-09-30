package net.ontrack.core.model;

import lombok.Data;

@Data
public class ValidationStamp implements Comparable<ValidationStamp> {

    private final int id;
    private final String name;
    private final String description;
    private final int branch;
    private final int orderNb;
    private final AccountSummary owner;

    @Override
    public int compareTo(ValidationStamp o) {
        return name.compareTo(o.name);
    }
}
