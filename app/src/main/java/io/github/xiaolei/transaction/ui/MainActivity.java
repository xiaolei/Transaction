package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.enterpriselibrary.utility.PhotoPicker;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.event.AccountInfoLoadCompletedEvent;
import io.github.xiaolei.transaction.event.AppInitCompletedEvent;
import io.github.xiaolei.transaction.event.NavigationDrawerStateEvent;
import io.github.xiaolei.transaction.event.PickPhotoEvent;
import io.github.xiaolei.transaction.widget.AccountView;


public class MainActivity extends BaseActivity
        implements ITitleChangeable, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int VIEW_INDEX_SPLASH = 0;
    public static final int VIEW_INDEX_CONTENT = 1;

    private ViewHolder mViewHolder;
    private HashMap<Integer, String> mMenuItemAndFragmentMapping = new HashMap<Integer, String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        mViewHolder = new ViewHolder(this);

        mMenuItemAndFragmentMapping.put(R.id.navigation_item_calculator, CalculatorFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_dashboard, DashboardFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_transactions, TransactionNavigationFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_analysis, AnalysisFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_products, ProductsFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_tags, TagsFragment.class.getName());

        start();
    }

    private String getMenuItemRelatedFragmentTagName(int menuItemId) {
        if (mMenuItemAndFragmentMapping.containsKey(menuItemId)) {
            return mMenuItemAndFragmentMapping.get(menuItemId);
        }

        return null;
    }

    private int getMenuItemIdByFragmentTagName(String fragmentTagName){
        for(Map.Entry<Integer, String> entry: mMenuItemAndFragmentMapping.entrySet()){
            if(entry.getValue().equalsIgnoreCase(fragmentTagName)){
                return entry.getKey();
            }
        }

        return -1;
    }

    @Override
    protected void onFragmentPoppedFromBackStack(BaseFragment fragment){
        int menuId = getMenuItemIdByFragmentTagName(fragment.getClass().getName());
        if(menuId > 0){
            mViewHolder.navigationView.setCheckedItem(menuId);
        }
    }

    private void start() {
        if (GlobalApplication.isInitialized()) {
            initialize();
            switchToContentView();
        } else {
            switchToSplashView();
        }
    }

    private void initialize() {
        Logger.d(TAG, "initialize");

        mViewHolder.navigationView.setNavigationItemSelectedListener(this);
        setupToolbar(mViewHolder.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mViewHolder.drawerLayout, mViewHolder.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mViewHolder.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        boolean goAhead = true;

        if (mViewHolder.drawerLayout.isDrawerOpen(Gravity.LEFT)) {
            mViewHolder.drawerLayout.closeDrawers();
            goAhead = false;
        }

        if (goAhead) {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    public void switchToHomeFragment() {
        switchToFragment(CalculatorFragment.class, null);
        mViewHolder.navigationView.setCheckedItem(R.id.navigation_item_calculator);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void setActionBarTitle(int resId) {
        this.getSupportActionBar().setTitle(getString(resId));
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mViewHolder.drawerLayout.closeDrawers();
        String fragmentTagName = getMenuItemRelatedFragmentTagName(menuItem.getItemId());
        switchToFragment(fragmentTagName, null);

        return true;
    }

    public void onEvent(AccountInfoLoadCompletedEvent event) {
        showAccountInfo();
    }

    public void onEvent(NavigationDrawerStateEvent event) {
        if (mViewHolder == null) {
            return;
        }

        if (event.visibility) {
            mViewHolder.drawerLayout.openDrawer(GravityCompat.START);
        } else {
            mViewHolder.drawerLayout.closeDrawers();
        }
    }

    public void onEvent(AppInitCompletedEvent event) {
        Logger.d(TAG, "Receive Application initialized completed event");
        initialize();
        switchToContentView();
    }

    private void showAccountInfo() {
        if (mViewHolder != null) {
            mViewHolder.accountView.bind(GlobalApplication.getCurrentAccount());
        }
    }

    private void switchToSplashView() {
        mViewHolder.toolbar.setVisibility(View.GONE);
        mViewHolder.mainViewFlipper.setDisplayedChild(VIEW_INDEX_SPLASH);
        mViewHolder.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }

    private void switchToContentView() {
        mViewHolder.toolbar.setVisibility(View.VISIBLE);
        mViewHolder.mainViewFlipper.setDisplayedChild(VIEW_INDEX_CONTENT);
        mViewHolder.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        switchToHomeFragment();
    }

    private class ViewHolder {
        public FrameLayout fragmentContainer;
        public NavigationView navigationView;
        public DrawerLayout drawerLayout;
        public AccountView accountView;
        public ViewFlipper mainViewFlipper;
        public Toolbar toolbar;

        public ViewHolder(Activity activity) {
            fragmentContainer = (FrameLayout) activity.findViewById(R.id.fragmentContainer);
            navigationView = (NavigationView) activity.findViewById(R.id.navigationView);
            drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
            accountView = (AccountView) activity.findViewById(R.id.accountView);
            mainViewFlipper = (ViewFlipper) activity.findViewById(R.id.mainViewFlipper);
            toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == PhotoPicker.IMAGE_PICK
                || requestCode == PhotoPicker.IMAGE_CAPTURE)) {
            String photoFileName = "";
            switch (requestCode) {
                case PhotoPicker.IMAGE_PICK:
                    photoFileName = PhotoPicker.getInstance().extractImageUrlFromGallery(this, data);
                    Toast.makeText(this, photoFileName, Toast.LENGTH_SHORT).show();
                    break;

                case PhotoPicker.IMAGE_CAPTURE:
                    photoFileName = PhotoPicker.getInstance().getCameraPhotoFileName();
                    Toast.makeText(this, photoFileName, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

            if (!TextUtils.isEmpty(photoFileName)) {
                EventBus.getDefault().post(new PickPhotoEvent(photoFileName));
            }
        }
    }
}
