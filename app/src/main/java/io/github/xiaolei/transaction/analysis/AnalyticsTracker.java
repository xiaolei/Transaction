package io.github.xiaolei.transaction.analysis;

import android.content.Context;

/**
 * Defines the common behavior of a analytics tracker.
 */
public interface AnalyticsTracker {
    int getId();

    void initialize();

    void onPause(Context context);

    void onResume(Context context);
}
