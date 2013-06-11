package net.ontrack.core.model;

import com.google.common.collect.ImmutableList;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class DashboardPage {

    private final String title;
    private final List<DashboardSection> sections;

    public static DashboardPage create(String title) {
        return new DashboardPage(title, Collections.<DashboardSection>emptyList());
    }

    public DashboardPage withSection(DashboardSection section) {
        List<DashboardSection> target = new ArrayList<>(sections);
        target.add(section);
        return new DashboardPage(title, ImmutableList.copyOf(target));
    }
}
