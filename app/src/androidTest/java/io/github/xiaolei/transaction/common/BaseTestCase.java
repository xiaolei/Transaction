package io.github.xiaolei.transaction.common;

import android.content.Context;
import android.test.AndroidTestCase;

/**
 * TODO: add comment
 */
public abstract class BaseTestCase extends AndroidTestCase {
    private Context mContext;

    public Context getTestContext() {
        return mContext;
    }

    @Override
    public void setUp() {
        this.mContext = TestUtility.getRenamingDelegatingContext(this.getContext());
    }

}
