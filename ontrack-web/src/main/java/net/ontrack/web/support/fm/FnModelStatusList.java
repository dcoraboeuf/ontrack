package net.ontrack.web.support.fm;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;
import net.ontrack.core.model.Status;
import org.apache.commons.lang3.Validate;

import java.util.Arrays;
import java.util.List;

public class FnModelStatusList implements TemplateMethodModel {

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List list) throws TemplateModelException {
        // Checks
        Validate.notNull(list, "List of arguments is required");
        Validate.isTrue(list.isEmpty(), "List of arguments must be empty");
        // List of values
        return Lists.transform(Arrays.asList(Status.values()), Functions.toStringFunction());
    }

}
