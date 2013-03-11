package net.ontrack.core.model;

import lombok.Data;

@Data
public class BuildPromotionLevel {

    private final DatedSignature signature;
    private final String name;
    private final String description;
    private final int levelNb;

}
