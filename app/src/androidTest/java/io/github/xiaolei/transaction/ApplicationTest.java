package io.github.xiaolei.transaction;

import android.app.Application;
import android.test.ApplicationTestCase;
import android.util.Log;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<GlobalApplication> {
    public ApplicationTest() {
        super(GlobalApplication.class);
    }

    public void testApplication() {
        Log.d("ApplicationTest", "testApplication");
    }
}