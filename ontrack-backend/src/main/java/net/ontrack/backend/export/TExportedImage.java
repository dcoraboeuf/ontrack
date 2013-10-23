package net.ontrack.backend.export;

import lombok.Data;

@Data
public class TExportedImage {

    private final int id;
    private final byte[] bytes;

}
