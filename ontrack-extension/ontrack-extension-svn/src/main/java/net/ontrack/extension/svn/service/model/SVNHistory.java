package net.ontrack.extension.svn.service.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Data
@AllArgsConstructor
public class SVNHistory {

    private final List<SVNReference> references;

    public SVNHistory(SVNReference reference) {
        this(Collections.singletonList(reference));
    }

    public SVNHistory add(SVNReference reference) {
        List<SVNReference> target = new ArrayList<>(references);
        target.add(reference);
        return new SVNHistory(target);
    }

}
