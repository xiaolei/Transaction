package io.github.xiaolei.transaction.viewmodel;

import java.math.BigDecimal;
import java.util.Date;

import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.util.PreferenceHelper;

/**
 * TODO: add comment
 */
public class CalculatorOutputInfo {
    public TransactionType transactionType;
    public String productName;
    public String currencyCode;
    public String tips;
    public BigDecimal price;
    public int quantity = 1;
    public Date date;
    public Product product;

    public CalculatorOutputInfo() {
        transactionType = TransactionType.Unknown;
        productName = "";
        this.currencyCode = PreferenceHelper.DEFAULT_CURRENCY_CODE;
        tips = "";
        price = new BigDecimal("0");
    }
}
