package net.ontrack.web.api.model;

import lombok.Data;

@Data
public class Name implements Named {

    private final String id;
    private final String name;

}
