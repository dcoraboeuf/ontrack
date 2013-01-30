package net.ontrack.web.ui;

import java.util.List;

import net.ontrack.core.model.ExpandedEvent;
import net.ontrack.core.ui.EventUI;
import net.ontrack.service.EventService;
import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class EventUIController extends AbstractUIController implements EventUI {

	private final EventService auditService;

	@Autowired
	public EventUIController(ErrorHandler errorHandler, Strings strings, EventService auditService) {
		super(errorHandler, strings);
		this.auditService = auditService;
	}
	
	@Override
	@RequestMapping(value = "/ui/event/all", method = RequestMethod.GET)
	public @ResponseBody List<ExpandedEvent> all (@RequestParam(required = false, defaultValue = "0") int offset, @RequestParam(required = false, defaultValue = "20") int count) {
		return auditService.all(offset, count);
	}

}
