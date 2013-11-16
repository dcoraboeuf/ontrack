package net.ontrack.web.api.input;

import lombok.Data;
import org.hibernate.validator.constraints.NotBlank;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class ProjectCreation {

    @NotNull
    @NotBlank
    @Pattern(regexp = "[A-Za-z0-9_\\.\\-]+")
    @Max(80)
    private final String name;
    @Max(1000)
    private final String description;

}
