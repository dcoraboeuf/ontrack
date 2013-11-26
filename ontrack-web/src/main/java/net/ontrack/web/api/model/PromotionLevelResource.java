package net.ontrack.web.api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = false)
public class PromotionLevelResource extends AbstractResource<PromotionLevelResource> {
    private final int id;
    private final String project;
    private final String branch;
    private final int levelNb;
    private final String name;
    private final String description;
    private final boolean autoPromote;

}
