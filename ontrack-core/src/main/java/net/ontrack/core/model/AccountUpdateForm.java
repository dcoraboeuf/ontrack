package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.validation.AccountValidation;

@Data
public class AccountUpdateForm implements AccountValidation {

    private String name;
    private String fullName;
    private String email;
    private String roleName;

}
