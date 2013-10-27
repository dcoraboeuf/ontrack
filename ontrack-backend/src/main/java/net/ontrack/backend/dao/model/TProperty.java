package net.ontrack.backend.dao.model;

import lombok.Data;
import net.ontrack.core.model.Entity;

import java.util.Map;

@Data
public class TProperty {

    private final int id;
    private final String extension;
    private final String name;
    private final String value;
    private final Map<Entity, Integer> entities;

}
