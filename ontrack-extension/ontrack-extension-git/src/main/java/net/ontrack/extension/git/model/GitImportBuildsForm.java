package net.ontrack.extension.git.model;

import lombok.Data;

@Data
public class GitImportBuildsForm {

    private boolean override;
    private String tagPattern;

}
