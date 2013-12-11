package net.ontrack.web.api.input;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class AccountCreation {

    @NotNull
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9]+")
    @Max(80)
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private final String password;
    private final String passwordConfirm;

}
