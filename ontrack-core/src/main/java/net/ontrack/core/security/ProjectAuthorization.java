package net.ontrack.core.security;

import lombok.Data;
import net.ontrack.core.model.AccountSummary;

@Data
public class ProjectAuthorization {

    private final int project;
    private final AccountSummary account;
    private final ProjectRole role;

}
