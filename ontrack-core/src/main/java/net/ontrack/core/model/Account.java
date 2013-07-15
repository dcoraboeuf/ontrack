package net.ontrack.core.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Locale;

@Data
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class Account {

    private final int id;
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private Locale locale;

}
