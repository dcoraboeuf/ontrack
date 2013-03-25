package net.ontrack.extension.svn;

import lombok.Data;

@Data
public class IndexationConfiguration {

    private int scanInterval;
    private long startRevision;

}
