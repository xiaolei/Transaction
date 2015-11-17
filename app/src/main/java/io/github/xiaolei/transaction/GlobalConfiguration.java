package io.github.xiaolei.transaction;

import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;

/**
 * TODO: add comment
 */
public final class GlobalConfiguration {
    public static final boolean DEBUG = true;

    public static String getDefaultCurrencyCode() {
        return GlobalApplication.getCurrentAccount() != null ?
                GlobalApplication.getCurrentAccount().getDefaultCurrencyCode() :
                CurrencyHelper.getDefaultCurrencyCode();
    }
}
