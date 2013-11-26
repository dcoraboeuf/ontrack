package net.ontrack.web.api.assembly;

import net.ontrack.core.security.SecurityUtils;

public abstract class AbstractAssembler {

    protected final SecurityUtils securityUtils;

    protected AbstractAssembler(SecurityUtils securityUtils) {
        this.securityUtils = securityUtils;
    }
}
