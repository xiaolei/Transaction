package io.github.xiaolei.transaction.common;

import android.content.Context;
import android.test.RenamingDelegatingContext;

/**
 * TODO: Add a class header comment!
 */
public final class TestUtility {
    private static final String TEST_FILE_PREFIX = "test_";
    private static RenamingDelegatingContext mContext;

    public static Context getRenamingDelegatingContext(Context context) {
        if (mContext == null) {
            mContext = new RenamingDelegatingContext(context, TEST_FILE_PREFIX);
        }
        return mContext;
    }
}
