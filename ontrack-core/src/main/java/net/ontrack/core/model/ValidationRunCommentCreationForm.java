package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class ValidationRunCommentCreationForm {

    private final String status;
    private final String description;
    private final List<PropertyCreationForm> properties;

}
