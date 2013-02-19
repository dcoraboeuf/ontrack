package net.ontrack.core.model;

import lombok.Data;

@Data
public class AccountCreationForm {

    private final String name;
    private final String fullName;
    private final String roleName;
    private final String mode;
    private final String password;
    private final String passwordConfirm;

}
