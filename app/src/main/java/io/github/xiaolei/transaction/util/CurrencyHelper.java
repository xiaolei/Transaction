package io.github.xiaolei.transaction.util;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

import io.github.xiaolei.transaction.GlobalApplication;

/**
 * TODO: add comment
 */
public class CurrencyHelper {
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
        formatter.setMaximumFractionDigits(ConfigurationManager.DECIMAL_POINT_LEFT);

        return formatter.format(money);
    }

    public static String formatCurrency(String currencyCode, int money) {
        BigDecimal value = new BigDecimal(money).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
        return formatCurrency(currencyCode, value);
    }

    public static String getDefaultCurrencyCode() {
        String result = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
        return !TextUtils.isEmpty(result) ? result : "USD";
    }

    public static BigDecimal castToBigDecimal(int money) {
        return new BigDecimal(money).movePointLeft(ConfigurationManager.DECIMAL_POINT_LEFT);
    }

    public static int getIntValue(BigDecimal money) {
        return money.movePointRight(ConfigurationManager.DECIMAL_POINT_LEFT).intValue();
    }
}
