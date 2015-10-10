package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * TODO: add comment
 */
public class PreferenceHelper {
    private static PreferenceHelper INSTANCE;
    private Context mContext;
    private SharedPreferences mPreference;

    public static final String PREF_KEY_CURRENT_ACCOUNT_ID = "account_id";
    public static final String DEFAULT_CURRENCY_CODE = "USD";

    private PreferenceHelper() {
    }

    static {
        INSTANCE = new PreferenceHelper();
    }

    private void setContext(Context context) {
        mContext = context;
        mPreference = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public synchronized static PreferenceHelper getInstance(Context context) {
        if (context != INSTANCE.mContext) {
            INSTANCE.setContext(context);
        }

        return INSTANCE;
    }

    public SharedPreferences getSharedPreferences() {
        return mPreference;
    }

    public void setPreferenceValue(String key, Object value) {
        if (value instanceof String) {
            mPreference.edit().putString(key, String.valueOf(value)).commit();
        } else if (value instanceof Integer) {
            mPreference.edit().putInt(key, Integer.parseInt(String.valueOf(value))).commit();
        } else if (value instanceof Float) {
            mPreference.edit().putFloat(key, Float.parseFloat(String.valueOf(value))).commit();
        } else if (value instanceof Long) {
            mPreference.edit().putLong(key, Long.parseLong(String.valueOf(value))).commit();
        } else if (value instanceof Boolean) {
            mPreference.edit().putBoolean(key, Boolean.parseBoolean(String.valueOf(value))).commit();
        } else {
            mPreference.edit().putString(key, String.valueOf(value)).commit();
        }
    }
}
