package net.ontrack.core.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChartTable {

    private final Map<String, Map<String, Integer>> support = new HashMap<>();

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

    public Map<String, Map<String, Integer>> getTable() {
        return support;
    }
}
