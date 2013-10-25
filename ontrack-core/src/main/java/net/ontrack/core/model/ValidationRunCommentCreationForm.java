package net.ontrack.core.model;

import org.apache.commons.lang3.EnumUtils;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class ValidationRunCommentCreationForm {

    private final Status status;
    private final String description;
    private final List<PropertyCreationForm> properties;

    private ValidationRunCommentCreationForm(Status status, String description, List<PropertyCreationForm> properties) {
        this.status = status;
        this.description = description;
        this.properties = properties;
    }

    @JsonCreator
    public ValidationRunCommentCreationForm(
            @JsonProperty("status")
            String status,
            @JsonProperty("description")
            String description,
            @JsonProperty("properties")
            List<PropertyCreationForm> properties) {
        this(EnumUtils.getEnum(Status.class, status), description, properties);
    }

    public static ValidationRunCommentCreationForm of(Status status, String description, List<PropertyCreationForm> properties) {
        return new ValidationRunCommentCreationForm(
                status,
                description,
                properties
        );
    }

    public Status getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public List<PropertyCreationForm> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        return "ValidationRunCommentCreationForm{" +
                "status=" + status +
                ", description='" + description + '\'' +
                ", properties=" + properties +
                '}';
    }
}
