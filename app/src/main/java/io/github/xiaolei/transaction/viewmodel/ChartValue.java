package io.github.xiaolei.transaction.viewmodel;

import java.math.BigDecimal;

/**
 * TODO: add comment
 */
public class ChartValue {
    public String xValue;
    public BigDecimal yValue;

    public ChartValue(String xValue, BigDecimal yValue){
        this.xValue = xValue;
        this.yValue = yValue;
    }
}
