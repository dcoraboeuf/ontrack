package net.ontrack.backend.dao.model;

import lombok.Data;

@Data
public class TValidationStamp {

    private final int id;
    private final int branch;
    private final String name;
    private final String description;
    private final Integer promotionLevel;
    private final int orderNb;
    private final Integer ownerId;

}
