package net.ontrack.core.model;

import lombok.Data;

import java.util.List;

/**
 * Form used to create/update a custom dashboard.
 */
@Data
public class DashboardConfigForm {

    private final String name;
    private final List<Integer> branches;

}
