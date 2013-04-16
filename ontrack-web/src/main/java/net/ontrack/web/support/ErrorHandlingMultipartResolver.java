package net.ontrack.web.support;

import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.http.HttpServletRequest;

public interface ErrorHandlingMultipartResolver extends MultipartResolver {
    void checkForUploadError(HttpServletRequest request);
}
