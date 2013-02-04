package net.ontrack.web.ui;

import net.ontrack.web.support.AbstractUIController;
import net.ontrack.web.support.EntityConverter;
import net.ontrack.web.support.ErrorHandler;
import net.sf.jstring.Strings;

public class AbstractEntityUIController extends AbstractUIController {

	protected final EntityConverter entityConverter;

	public AbstractEntityUIController(ErrorHandler errorHandler, Strings strings, EntityConverter entityConverter) {
		super(errorHandler, strings);
		this.entityConverter = entityConverter;
	}

}
