package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TPromotedRun {

    private final int id;
    private final int build;
    private final int promotionLevel;
    private final String description;

}
