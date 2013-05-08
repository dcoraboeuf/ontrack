package net.ontrack.core.model;

import lombok.Data;

@Data
public class Comment {

    private final int id;
    private final String comment;
    private final DatedSignature signature;

}
