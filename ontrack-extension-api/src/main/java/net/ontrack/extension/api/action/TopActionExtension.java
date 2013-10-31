package net.ontrack.extension.api.action;

import net.ontrack.core.security.AuthorizationPolicy;

public interface TopActionExtension {

    String getExtension();

    String getName();

    AuthorizationPolicy getAuthorizationPolicy();

    String getPath();

    String getTitleKey();
}
