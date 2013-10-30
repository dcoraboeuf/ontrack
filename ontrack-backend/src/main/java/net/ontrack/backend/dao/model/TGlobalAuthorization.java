package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.security.GlobalFunction;

@Data
public class TGlobalAuthorization {

    private final int account;
    private final GlobalFunction fn;

}
