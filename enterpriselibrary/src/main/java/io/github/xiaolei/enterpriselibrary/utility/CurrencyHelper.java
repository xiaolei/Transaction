package io.github.xiaolei.enterpriselibrary.utility;

import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;

/**
 * TODO: add comment
 */
public class CurrencyHelper {
    public static final int MAXIMUM_FRACTION_DIGITS = 2;

    /**
     * Format currency.
     *
     * @param money
     * @return
     */
    public static String formatCurrency(String currencyCode, BigDecimal money) {
        if (money == null || TextUtils.isEmpty(currencyCode)) {
            money = BigDecimal.ZERO;
        }

        DecimalFormat formatter = (DecimalFormat) NumberFormat.getCurrencyInstance();
        formatter.setCurrency(Currency.getInstance(currencyCode));
        formatter.setMaximumFractionDigits(MAXIMUM_FRACTION_DIGITS);

        return formatter.format(money);
    }

    public static String getDefaultCurrencyCode() {
        return "USD";
    }

    public static BigDecimal castToBigDecimal(int money) {
        return new BigDecimal(money).movePointLeft(MAXIMUM_FRACTION_DIGITS);
    }
}
