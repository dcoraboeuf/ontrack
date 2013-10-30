package net.ontrack.core.model;

import lombok.Data;
import net.ontrack.core.security.GlobalFunction;

import java.util.List;

@Data
public class GlobalACLSummary {

    private final Account account;
    private final List<GlobalFunction> fns;

}
