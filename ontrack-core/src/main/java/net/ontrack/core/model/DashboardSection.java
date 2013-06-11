package net.ontrack.core.model;

import lombok.Data;

import java.util.UUID;

@Data
public class DashboardSection {

    private final String uuid;
    private final String templateId;
    private final Object data;

    public DashboardSection(String templateId, Object data) {
        this.uuid = UUID.randomUUID().toString();
        this.templateId = templateId;
        this.data = data;
    }
}
