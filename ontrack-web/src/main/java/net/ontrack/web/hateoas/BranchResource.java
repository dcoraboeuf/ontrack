package net.ontrack.web.hateoas;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.ResourceSupport;

@Data
@EqualsAndHashCode(callSuper = false)
public class BranchResource extends ResourceSupport {

    private final int branchId;
    private final String name;
    private final String description;

}
