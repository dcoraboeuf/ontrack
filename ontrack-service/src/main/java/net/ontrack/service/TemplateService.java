package net.ontrack.service;


import net.ontrack.service.model.TemplateModel;

import java.util.Locale;

public interface TemplateService {

    String generate(String templateId, Locale locale, TemplateModel templateModel);

}
