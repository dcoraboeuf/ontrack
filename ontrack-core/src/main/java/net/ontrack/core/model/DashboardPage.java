package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

@Data
public class DashboardPage {

    private final String title;
    private final List<DashboardSection> sections;
}
