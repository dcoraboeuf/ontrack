package net.ontrack.web.support;

import javax.servlet.http.HttpServletRequest;

public interface ErrorHandlingMultipartResolver {
    void checkForUploadError(HttpServletRequest request);
}
