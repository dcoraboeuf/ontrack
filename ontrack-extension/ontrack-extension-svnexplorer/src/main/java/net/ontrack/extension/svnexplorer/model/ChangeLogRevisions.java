package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

import java.util.Collections;
import java.util.List;

@Data
public class ChangeLogRevisions {

    private final List<ChangeLogRevision> list;

    public static ChangeLogRevisions none() {
        return new ChangeLogRevisions(Collections.<ChangeLogRevision>emptyList());
    }

}
