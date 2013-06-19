package net.ontrack.extension.git.client.plot;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class GPoint {

    private final int x;
    private final int y;

    public static GPoint of(int x, int y) {
        return new GPoint(x, y);
    }

    public GPoint ty(int offset) {
        return of(x, y + offset);
    }
}
