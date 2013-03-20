package net.ontrack.web.ui.model;

import lombok.Data;
import net.ontrack.core.model.EditableProperty;
import net.ontrack.core.model.Status;

import java.util.List;

@Data
public class ValidationRunStatusUpdateData {

    private final List<Status> nextStatusList;
    private final List<EditableProperty> editableProperties;

}
