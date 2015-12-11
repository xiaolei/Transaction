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
    public static final String FREQUENCY = "frequency"; // 使用频率，每购买一次此产品，在原有值基础上+FREQUENCY_STEP
    public static final double FREQUENCY_STEP = 0.0001d;

    @DatabaseField(canBeNull = false, columnName = CURRENCY_CODE)
    private String currencyCode;

    @DatabaseField(canBeNull = false, columnName = EXCHANGE_RATE)
    private int exchangeRate;

    @DatabaseField
    private double frequency;

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

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }
}
