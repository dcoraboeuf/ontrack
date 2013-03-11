package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TPromotionLevel {

    private final int id;
    private final int branch;
    private final int levelNb;
    private final String name;
    private final String description;

}
