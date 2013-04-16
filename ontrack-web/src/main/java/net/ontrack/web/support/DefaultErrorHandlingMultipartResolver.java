package net.ontrack.web.support;

import org.apache.commons.fileupload.FileItem;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

public class DefaultErrorHandlingMultipartResolver extends CommonsMultipartResolver implements ErrorHandlingMultipartResolver {

    protected static final String EXCEPTION_KEY = "MULTIPART.EXCEPTION";

    private final long sizeInK;

    public DefaultErrorHandlingMultipartResolver(long sizeInK) {
        this.sizeInK = sizeInK;
        setMaxUploadSize(sizeInK * 1024);
    }

    @Override
    protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        try {
            return super.parseRequest(request);
        } catch (MaxUploadSizeExceededException ex) {
            String encoding = determineEncoding(request);
            List<FileItem> fileItems = Collections.emptyList();
            request.setAttribute(EXCEPTION_KEY, ex);
            return parseFileItems(fileItems, encoding);
        }
    }

    @Override
    public void checkForUploadError(HttpServletRequest request) {
        Exception ex = (Exception) request.getAttribute(EXCEPTION_KEY);
        if (ex != null) {
            if (ex instanceof MaxUploadSizeExceededException) {
                throw new UploadTooBigException(sizeInK);
            }
        }
    }
}
