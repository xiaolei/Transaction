package io.github.xiaolei.transaction.common;

import android.text.TextUtils;

import java.math.BigDecimal;

/**
 * TODO: add comment
 */
public class ValidationHelper {
    public static boolean isValidInteger(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }

        try {
            Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }

    public static boolean isValidBigDecimal(String value) {
        if (TextUtils.isEmpty(value)) {
            return false;
        }

        try {
            BigDecimal money = new BigDecimal(value);
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
