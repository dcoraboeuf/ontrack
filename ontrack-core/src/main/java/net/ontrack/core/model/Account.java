package net.ontrack.core.model;

import lombok.Data;

@Data
public class Account {

    private final int id;
    private final String name;
    private final String fullName;
    private final String roleName;
    private final String mode;

}
