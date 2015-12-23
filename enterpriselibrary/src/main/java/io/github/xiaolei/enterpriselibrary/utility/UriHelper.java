package io.github.xiaolei.enterpriselibrary.utility;

import android.text.TextUtils;
import android.util.Patterns;

/**
 * TODO: add comment
 */
public class UriHelper {
    public static boolean isValidUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }

        return Patterns.WEB_URL.matcher(url).matches();
    }
}
