package net.ontrack.web.support;

import net.ontrack.core.model.UserMessage;
import net.ontrack.service.InfoService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Locale;

@Controller
@RequestMapping("/ui/info")
public class InfoUIController {

    private final InfoService infoService;
    private final Strings strings;

    @Autowired
    public InfoUIController(InfoService infoService, Strings strings) {
        this.infoService = infoService;
        this.strings = strings;
    }

    @RequestMapping(value = "/message", method = RequestMethod.GET)
    public
    @ResponseBody
    Alert info(Locale locale) {
        UserMessage info = infoService.getInfo();
        return new Alert(info.getType(), info.getMessage().getLocalizedMessage(strings, locale));
    }

}
