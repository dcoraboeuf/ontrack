package net.ontrack.core.ui;

import net.ontrack.core.model.EditableProperty;
import net.ontrack.core.model.Entity;

import java.util.List;
import java.util.Locale;

public interface PropertyUI {

    List<EditableProperty> getEditableProperties(
            Locale locale,
            Entity entity,
            int entityId);

}
