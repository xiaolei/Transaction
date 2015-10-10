package io.github.xiaolei.transaction.database;

import android.content.Context;
import android.test.AndroidTestCase;

import junit.framework.TestCase;

import io.github.xiaolei.transaction.common.TestUtility;

/**
 * TODO: add comment
 */
public class DatabaseHelperTest extends AndroidTestCase {
    private DatabaseHelper db;
    private Context mContext;

    public void setUp() {
        this.mContext = TestUtility.getRenamingDelegatingContext(this.getContext());
        db = DatabaseHelper.getInstance(this.mContext);

        assertNotNull(db);
    }

    public void tearDown() throws Exception {
        db.close();
        super.tearDown();
    }
}