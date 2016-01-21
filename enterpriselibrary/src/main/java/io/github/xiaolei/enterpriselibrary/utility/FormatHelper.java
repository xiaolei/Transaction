package io.github.xiaolei.enterpriselibrary.utility;

import java.text.DecimalFormat;

/**
 * TODO: add comment
 */
public class FormatHelper {
    public static String formatCorrectRate(double correctRate) {
        DecimalFormat format = new DecimalFormat("#.##");
        format.setDecimalSeparatorAlwaysShown(false);

        return format.format(correctRate);
    }

    public static double roundTwoDecimals(double d)
    {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }
}
