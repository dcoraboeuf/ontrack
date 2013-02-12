package net.ontrack.service.validation;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public interface LDAPConfigurationValidation {

    @NotNull
    @Size(min = 1, max = 200)
    String getHost();

    @NotNull
    @Min(1)
    Integer getPort();

    @NotNull
    @Size(min = 1, max = 200)
    String getSearchBase();

    @NotNull
    @Size(min = 1, max = 200)
    String getSearchFilter();

    @NotNull
    @Size(min = 1, max = 200)
    String getUser();

    @NotNull
    @Size(min = 1, max = 200)
    String getPassword();
}
