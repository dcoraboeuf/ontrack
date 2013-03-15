package net.ontrack.web.gui.model;

import lombok.Data;

@Data
public class BuildLogForm {

    private String withPromotionLevel;
    private String sincePromotionLevel;
    private String withValidationStamp;
    private String withValidationStampStatus;
    private String sinceValidationStamp;
    private String sinceValidationStampStatus;
    private int limit = -1;

}
