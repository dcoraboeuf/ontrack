package net.ontrack.web.gui;

import net.ontrack.web.support.AbstractGUIController;
import net.ontrack.web.support.ErrorHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeController extends AbstractGUIController {

	@Autowired
	public HomeController(ErrorHandler errorHandler) {
		super(errorHandler);
	}

	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home() {
		// OK
		return "home";
	}

}
