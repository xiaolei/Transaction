package io.github.xiaolei.transaction.analysis;

import java.util.HashMap;

/**
 * Manages all of the support analytics trackers.
 */
public class AnalyticsTrackerManager {
    private static AnalyticsTrackerManager INSTANCE;
    private HashMap<Integer, AnalyticsTracker> mTrackers;

    private AnalyticsTrackerManager() {
        mTrackers = new HashMap<>();
        registerAllSupportAnalyticsTrackers();
    }

    public synchronized static AnalyticsTrackerManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AnalyticsTrackerManager();
        }

        return INSTANCE;
    }

    public void register(AnalyticsTracker tracker) {
        if (mTrackers.containsKey(tracker.getId())) {
            mTrackers.remove(tracker.getId());
        }

        mTrackers.put(tracker.getId(), tracker);
    }

    public void unregister(int id) {
        if (mTrackers.containsKey(id)) {
            mTrackers.remove(id);
        }
    }

    private void registerAllSupportAnalyticsTrackers() {
        register(new UmengAnalyticsTracker());
    }

    public <T extends AnalyticsTracker> T getAnalyticsTracker(int id, Class<T> trackerType) {
        AnalyticsTracker tracker = mTrackers.get(id);
        if (tracker != null) {
            return trackerType.cast(tracker);
        } else {
            return null;
        }
    }

    public AnalyticsTracker getAnalyticsTracker(int id) {
        return mTrackers.get(id);
    }

    public AnalyticsTracker getDefaultTracker() {
        return getAnalyticsTracker(SupportAnalyticsTrackerId.UMENG);
    }
}
