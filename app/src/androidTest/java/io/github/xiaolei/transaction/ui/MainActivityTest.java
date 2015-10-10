package io.github.xiaolei.transaction.ui;

import android.test.ActivityInstrumentationTestCase2;

import io.github.xiaolei.transaction.util.PreferenceHelper;


/**
 * TODO: add comment
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mFirstTestActivity;


    public MainActivityTest() {
        super(MainActivity.class);
    }

    public void testOnCreate() throws Exception {
        assertNotNull(mFirstTestActivity);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mFirstTestActivity = getActivity();
    }

    /**
     * TODO: add comment
     */
    public static class PreferenceHelperTest extends ActivityInstrumentationTestCase2<MainActivity> {

        private MainActivity mActivity;

        public PreferenceHelperTest() {
            super(MainActivity.class);
        }

        private PreferenceHelper getPreferenceHelperInstance() {
            return PreferenceHelper.getInstance(getActivity());
        }

        @Override
        protected void setUp() throws Exception {
            super.setUp();
            setActivityInitialTouchMode(true);
            mActivity = getActivity();
        }

        public void testGetInstance() throws Exception {
            PreferenceHelper instance = PreferenceHelper.getInstance(getActivity());
            assertNotNull(instance);
        }

        public void testGetPreferenceValue() {
            String defaultStringValue = "default";
            String value = PreferenceHelper.getInstance(getActivity()).getSharedPreferences().getString("test", defaultStringValue);
            assertEquals(defaultStringValue, value);
        }

        public void testSavePreferenceValue() {
            String stringKey = "stringKey";
            String intKey = "intKey";
            String floatKey = "floatKey";
            String longKey = "longKey";
            String boolKey = "boolKey";

            String stringValue = "stringValue";
            int intValue = 1;
            float floatValue = 1.23f;
            long longValue = 9999L;
            boolean boolValue = true;

            getPreferenceHelperInstance().setPreferenceValue(stringKey, stringValue);
            assertEquals(stringValue, getPreferenceHelperInstance().getSharedPreferences().getString(stringKey, null));

            getPreferenceHelperInstance().setPreferenceValue(intKey, intValue);
            assertEquals(intValue, getPreferenceHelperInstance().getSharedPreferences().getInt(intKey, -1));

            getPreferenceHelperInstance().setPreferenceValue(floatKey, floatValue);
            assertEquals(floatValue, getPreferenceHelperInstance().getSharedPreferences().getFloat(floatKey, -1F))
            ;

            getPreferenceHelperInstance().setPreferenceValue(longKey, longValue);
            assertEquals(longValue, getPreferenceHelperInstance().getSharedPreferences().getLong(longKey, -1L));

            getPreferenceHelperInstance().setPreferenceValue(boolKey, boolValue);
            assertEquals(boolValue, getPreferenceHelperInstance().getSharedPreferences().getBoolean(boolKey, false));
        }
    }
}