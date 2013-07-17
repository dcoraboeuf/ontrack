package net.ontrack.core.model;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class ChartTable {

    private final Map<String, Map<String, Integer>> support = new LinkedHashMap<>();

    private ChartTable() {
    }

    public static ChartTable create(List<String> rows) {
        ChartTable table = new ChartTable();
        for (String row : rows) {
            table.support.put(row, new HashMap<String, Integer>());
        }
        return table;
    }

    public void put(String row, String column, int count) {
        support.get(row).put(column, count);
    }

    public List<Pair<String, Map<String, Integer>>> getTable() {
        List<Pair<String, Map<String, Integer>>> pairs = new ArrayList<>();
        for (Map.Entry<String, Map<String, Integer>> entry : support.entrySet()) {
            String stamp = entry.getKey();
            Map<String, Integer> statuses = entry.getValue();
            pairs.add(Pair.of(stamp, statuses));
        }
        return pairs;
    }
}
