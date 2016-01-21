package io.github.xiaolei.transaction.analysis;

import android.content.Context;

import com.umeng.analytics.MobclickAgent;

import io.github.xiaolei.transaction.GlobalConfiguration;

/**
 * UMENG analytics tracker.
 * http://www.umeng.com/analytics
 */
public class UmengAnalyticsTracker implements AnalyticsTracker {
    @Override
    public int getId() {
        return SupportAnalyticsTrackerId.UMENG;
    }

    @Override
    public void initialize() {
        // Ensure that UMENG doesn't collect the MAC address of the phone.
        MobclickAgent.setCheckDevice(false);
        MobclickAgent.setDebugMode(GlobalConfiguration.DEBUG);
        MobclickAgent.setSessionContinueMillis(30000);
    }

    @Override
    public void onPause(Context context) {
        MobclickAgent.onPause(context);
    }

    @Override
    public void onResume(Context context) {
        MobclickAgent.onResume(context);
    }
}
