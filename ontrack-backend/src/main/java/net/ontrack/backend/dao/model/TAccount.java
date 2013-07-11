package net.ontrack.backend.dao.model;

import lombok.Data;

import java.util.Locale;

@Data
public class TAccount {

    private final int id;
    private final String name;
    private final String fullName;
    private final String email;
    private final String roleName;
    private final String mode;
    private final Locale locale;

}
