package net.ontrack.core.model;

import com.google.common.base.Function;
import lombok.Data;

@Data
public class ValidationStampSummary implements Comparable<ValidationStampSummary> {

    public static final Function<ValidationStampSummary, ValidationStamp> toValidationStampFn = new Function<ValidationStampSummary, ValidationStamp>() {
        @Override
        public ValidationStamp apply(ValidationStampSummary o) {
            return new ValidationStamp(
                    o.getId(),
                    o.getName(),
                    o.getDescription(),
                    o.getBranch().getId(),
                    o.getOrderNb(),
                    o.getOwner()
            );
        }
    };
    private final int id;
    private final String name;
    private final String description;
    private final BranchSummary branch;
    private final int orderNb;
    private final AccountSummary owner;

    @Override
    public int compareTo(ValidationStampSummary o) {
        return name.compareTo(o.name);
    }
}
