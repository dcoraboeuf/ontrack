package net.ontrack.core.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DashboardStatus {

    private final String css;
    private final String iconPath;

    public DashboardStatus() {
        this("", "");
    }

    public static DashboardStatus css(String css) {
        return new DashboardStatus(css, null);
    }

    public static DashboardStatus icon(String icon) {
        return new DashboardStatus(null, icon);
    }

    public DashboardStatus addCss(String cls) {
        return new DashboardStatus(css + " " + cls, iconPath);
    }

    public DashboardStatus withIcon(String icon) {
        return new DashboardStatus(css, icon);
    }
}
