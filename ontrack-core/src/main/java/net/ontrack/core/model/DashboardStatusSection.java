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
public class DashboardStatusSection {

    private final String title;
    private final List<DashboardStatus> statusList;

    public DashboardStatusSection(String title) {
        this(title, Collections.<DashboardStatus>emptyList());
    }

    public DashboardStatusSection withStatus(DashboardStatus status) {
        List<DashboardStatus> target = new ArrayList<>(statusList);
        target.add(status);
        return new DashboardStatusSection(title, ImmutableList.copyOf(target));
    }

}
