package net.ontrack.extension.git.model;

import lombok.Data;

import java.util.List;

@Data
public class ChangeLogFiles {

    private final List<ChangeLogFile> list;

}
