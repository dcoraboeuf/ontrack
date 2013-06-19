package net.ontrack.extension.git.client.plot;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class GPlot {

    private final List<GItem> items = new ArrayList<>();

    public GPlot add(GItem item) {
        items.add(item);
        return this;
    }
}
