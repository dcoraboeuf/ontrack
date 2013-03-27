package net.ontrack.extension.svnexplorer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SVNHistory {

    private final List<SVNReference> references;

    public SVNHistory add(SVNReference reference) {
        List<SVNReference> target = new ArrayList<>(references);
        target.add(reference);
        return new SVNHistory(target);
    }

}
