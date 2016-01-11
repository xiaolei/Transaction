package io.github.xiaolei.enterpriselibrary.utility;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

/**
 * TODO: add comment
 */
public class ViewHelper {
    public static Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }
}
