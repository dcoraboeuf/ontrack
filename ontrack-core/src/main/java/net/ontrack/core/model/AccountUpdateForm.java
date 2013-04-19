package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.validation.AccountValidation;

@Data
public class AccountUpdateForm implements AccountValidation {

    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;

}
