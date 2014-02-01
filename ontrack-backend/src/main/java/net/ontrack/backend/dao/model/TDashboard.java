package net.ontrack.backend.dao.model;

import lombok.Data;

import java.util.List;

@Data
public class TDashboard {

    private final int id;
    private final String name;
    private final List<Integer> branches;

}
