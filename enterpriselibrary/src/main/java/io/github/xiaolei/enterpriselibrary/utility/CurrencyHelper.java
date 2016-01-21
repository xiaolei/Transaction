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

    public static String formatCurrency(String currencyCode, int money) {
        BigDecimal value = new BigDecimal(money).movePointLeft(MAXIMUM_FRACTION_DIGITS);
        return formatCurrency(currencyCode, value);
    }

    public static String getDefaultCurrencyCode() {
        return "USD";
    }

    public static BigDecimal castToBigDecimal(int money) {
        return new BigDecimal(money).movePointLeft(MAXIMUM_FRACTION_DIGITS);
    }

    public static int castToInteger(BigDecimal money) {
        return money.movePointRight(MAXIMUM_FRACTION_DIGITS).intValue();
    }

    /**
     * 格式化显示金额
     *
     * @param money                    金额
     * @param showCurrencySymbol       是否显示货币符号
     * @param alwaysShowFractionDigits 是否一直显示小数，即使金额为整数值
     * @return 显示金额
     */
    public static String formatCurrency(BigDecimal money, String currencyCode, boolean showCurrencySymbol,
                                        boolean alwaysShowFractionDigits) {
        if (money == null || TextUtils.isEmpty(currencyCode)) {
            return "";
        }

        BigDecimal value = money;
        if (alwaysShowFractionDigits) {
            value = money.setScale(MAXIMUM_FRACTION_DIGITS, BigDecimal.ROUND_HALF_EVEN); // 不管有没有小数，都显示到后两位小数
        }

        String currencySymbol = Currency.getInstance(currencyCode).getSymbol();

        return showCurrencySymbol ? currencySymbol + value.toString() : value.toString();
    }

    public static boolean isGreaterThan(BigDecimal source, BigDecimal target) {
        return source.compareTo(target) == 1;
    }

    public static boolean isGreaterOrEquals(BigDecimal source, BigDecimal target) {
        int diff = source.compareTo(target);
        return diff == 1 || diff == 0;
    }

    public static boolean isLessThan(BigDecimal source, BigDecimal target) {
        int diff = source.compareTo(target);
        return diff == -1;
    }

    public static boolean isLessOrEquals(BigDecimal source, BigDecimal target) {
        int diff = source.compareTo(target);
        return diff == -1 || diff == 0;
    }

    public static boolean isIntegerValue(BigDecimal bd) {
        return bd.signum() == 0 || bd.scale() <= 0 || bd.stripTrailingZeros().scale() <= 0;
    }

    public static boolean isValidCurrency(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }

        try {
            BigDecimal money = new BigDecimal(text);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean isValidNumber(String text) {
        if (TextUtils.isEmpty(text)) {
            return false;
        }
        try {
            Integer.parseInt(text);
            return true;
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return false;
    }
}
