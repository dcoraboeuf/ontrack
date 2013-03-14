package net.ontrack.extension.api.support;

import net.ontrack.core.support.InputException;
import net.ontrack.extension.api.PropertyExtensionDescriptor;

public abstract class AbstractPropertyExtensionDescriptor implements PropertyExtensionDescriptor {

    /**
     * Does not validate any thing by default.
     */
    @Override
    public void validate(String value) throws InputException {
    }

}
