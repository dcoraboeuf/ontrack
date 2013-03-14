package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TProperty {

    private final int id;
    private final String extension;
    private final String name;
    private final String value;

}
