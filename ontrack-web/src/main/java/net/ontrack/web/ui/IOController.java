package net.ontrack.web.ui;

import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/ui/io")
public class IOController extends AbstractUIController {

    @Autowired
    public IOController(ErrorHandler errorHandler, Strings strings) {
        super(errorHandler, strings);
    }

    @RequestMapping(value = "/project/{project:[A-Za-z0-9_\\.\\-]+}", method = RequestMethod.GET)
    public void exportProject(@PathVariable String project, HttpServletResponse response) throws IOException {
        // FIXME Gets the export file
        response.sendError(HttpServletResponse.SC_NO_CONTENT);
    }

}
