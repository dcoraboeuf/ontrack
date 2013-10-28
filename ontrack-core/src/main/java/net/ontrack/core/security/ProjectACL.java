package net.ontrack.core.security;

import lombok.Data;

@Data
public class ProjectACL {

    private final ProjectFunction fn;
    private final int id;

}
