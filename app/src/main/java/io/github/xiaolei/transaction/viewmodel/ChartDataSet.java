package io.github.xiaolei.transaction.viewmodel;

import java.util.List;

/**
 * TODO: add comment
 */
public class ChartDataSet {
    public List<ChartValue> values;
    public int color;
    public String legend;

    public ChartDataSet(List<ChartValue> values, int color, String legend){
        this.values = values;
        this.color = color;
        this.legend = legend;
    }
}
