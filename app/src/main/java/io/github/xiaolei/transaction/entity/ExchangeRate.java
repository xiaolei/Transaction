package io.github.xiaolei.transaction.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * TODO: add comment
 */
@DatabaseTable(tableName = "exchange_rate")
public class ExchangeRate extends TableEntity {
    public static final String CURRENCY_CODE = "currency_code";
    public static final String EXCHANGE_RATE = "exchange_rate";

    @DatabaseField(canBeNull = false, columnName = CURRENCY_CODE)
    private String currencyCode;

    @DatabaseField(canBeNull = false, columnName = EXCHANGE_RATE)
    private int exchangeRate;

    public ExchangeRate() {
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(int exchangeRate) {
        this.exchangeRate = exchangeRate;
    }
}
