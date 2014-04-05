package net.ontrack.web.support;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import net.ontrack.core.model.UserMessage;
import net.ontrack.service.InfoService;
import net.sf.jstring.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Collection;
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
    Collection<Alert> info(final Locale locale) {
        return Collections2.transform(
                infoService.getInfo(),
                new Function<UserMessage, Alert>() {
                    @Override
                    public Alert apply(UserMessage message) {
                        return new Alert(
                                message.getType(),
                                message.getMessage().getLocalizedMessage(strings, locale)
                        );
                    }
                }
        );
    }

}
