package net.ontrack.web.hateoas;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Wither;
import net.ontrack.core.model.ValidationStampSummary;

@Data
@EqualsAndHashCode(callSuper = false)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class ValidationStampResource extends AbstractResource<ValidationStampResource> {

    private final int validationStampId;
    private final String projectName;
    private final String branchName;
    private final String name;
    private final String description;
    private final int orderNb;
    @Wither
    private final AccountResource owner;

    public ValidationStampResource(ValidationStampSummary o) {
        this(o.getId(), o.getBranch().getProject().getName(), o.getBranch().getName(), o.getName(), o.getDescription(), o.getOrderNb(), null);
    }
}
