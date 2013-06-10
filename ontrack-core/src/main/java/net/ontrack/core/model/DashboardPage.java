package net.ontrack.core.model;

import lombok.Data;

@Data
public class DashboardPage {

    private final String title;

    public static DashboardPage create(String title) {
        return new DashboardPage(title);
    }
}
