package io.github.xiaolei.transaction.viewmodel;

import java.math.BigDecimal;

/**
 * TODO: add comment
 */
public class AmountInfo {
    public BigDecimal amount = BigDecimal.ZERO;
    public BigDecimal totalIncome = BigDecimal.ZERO;
    public BigDecimal totalExpense = BigDecimal.ZERO;
    public String currencyCode;

    public AmountInfo() {
    }

    public AmountInfo(BigDecimal amount, BigDecimal totalIncome, BigDecimal totalExpense, String currencyCode) {
        this.amount = amount;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.currencyCode = currencyCode;
    }
}
