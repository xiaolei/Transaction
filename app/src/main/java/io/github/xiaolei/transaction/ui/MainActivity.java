package io.github.xiaolei.transaction.ui;

import android.Manifest;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Account;
import io.github.xiaolei.transaction.event.AccountInfoLoadCompletedEvent;
import io.github.xiaolei.transaction.event.AppInitCompletedEvent;
import io.github.xiaolei.transaction.event.CheckPermissionEvent;
import io.github.xiaolei.transaction.event.NavigationDrawerStateEvent;
import io.github.xiaolei.transaction.event.PickPhotoEvent;
import io.github.xiaolei.transaction.repository.AccountRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.widget.AccountView;


public class MainActivity extends BaseActivity
        implements ITitleChangeable, NavigationView.OnNavigationItemSelectedListener {
    public static final String TAG = MainActivity.class.getSimpleName();
    public static final int VIEW_INDEX_SPLASH = 0;
    public static final int VIEW_INDEX_CONTENT = 1;

    private ViewHolder mViewHolder;
    private HashMap<Integer, String> mMenuItemAndFragmentMapping = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mViewHolder = new ViewHolder(this);

        mMenuItemAndFragmentMapping.put(R.id.navigation_item_calculator, CalculatorFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_dashboard, DashboardFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_transactions, TransactionNavigationFragment.class.getName());
        //mMenuItemAndFragmentMapping.put(R.id.navigation_item_analysis, AnalysisFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_products, ProductListFragment.class.getName());
        mMenuItemAndFragmentMapping.put(R.id.navigation_item_tags, TagListFragment.class.getName());

        start();
    }

    private String getMenuItemRelatedFragmentTagName(int menuItemId) {
        if (mMenuItemAndFragmentMapping.containsKey(menuItemId)) {
            return mMenuItemAndFragmentMapping.get(menuItemId);
        }

        return null;
    }

    protected int getMenuItemIdByFragmentTagName(String fragmentTagName) {
        for (Map.Entry<Integer, String> entry : mMenuItemAndFragmentMapping.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(fragmentTagName)) {
                return entry.getKey();
            }
        }

        return -1;
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
        } else {
            Fragment currentFragment = getCurrentFragment();
            if (currentFragment instanceof CalculatorFragment) {
                CalculatorFragment calculatorFragment = (CalculatorFragment) currentFragment;
                if (calculatorFragment.getCurrentViewIndex() != CalculatorFragment.VIEW_INDEX_PRODUCTS) {
                    calculatorFragment.switchToProductListView();
                    goAhead = false;
                }
            }
        }

        if (goAhead) {
            super.onBackPressed();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void switchToHomeFragment() {
        switchToFragment(CalculatorFragment.class, null);
        mViewHolder.navigationView.setCheckedItem(R.id.navigation_item_calculator);
    }

    @Override
    public void setActionBarTitle(int resId) {
        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(resId));
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        mViewHolder.drawerLayout.closeDrawers();
        String fragmentTagName = getMenuItemRelatedFragmentTagName(menuItem.getItemId());
        switchToFragment(fragmentTagName, null);

        return true;
    }

    public Toolbar getToolbar() {
        return mViewHolder.toolbar;
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

    public void onEvent(PickPhotoEvent event) {
        updateAccountAvatarAsync(GlobalApplication.getCurrentAccountId(), event.photoFileUri);
    }

    private void updateAccountAvatarAsync(final long accountId, final String photoUrl) {
        AsyncTask<Void, Void, Account> task = new AsyncTask<Void, Void, Account>() {

            @Override
            protected Account doInBackground(Void... params) {
                Account account = null;
                try {
                    account = RepositoryProvider.getInstance(MainActivity.this).resolve(AccountRepository.class)
                            .updateAvatar(accountId, photoUrl);
                } catch (SQLException e) {
                    e.printStackTrace();
                    return account;
                }

                return account;
            }

            @Override
            protected void onPostExecute(Account account) {
                if (account == null) {
                    Toast.makeText(MainActivity.this, getString(R.string.error_failed_to_update_avator), Toast.LENGTH_SHORT).show();
                    return;
                }

                GlobalApplication.setCurrentAccount(account);
                mViewHolder.accountView.bind(account);
            }
        };

        task.execute();
    }

    private void showAccountInfo() {
        if (mViewHolder != null && mViewHolder.accountView != null) {
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
        showAccountInfo();
        switchToHomeFragment();

        EventBus.getDefault().post(new CheckPermissionEvent(Manifest.permission.WRITE_EXTERNAL_STORAGE));
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
            accountView = new AccountView(activity);
            navigationView.addHeaderView(accountView);
            mainViewFlipper = (ViewFlipper) activity.findViewById(R.id.mainViewFlipper);
            toolbar = (Toolbar) activity.findViewById(R.id.toolbar);
        }
    }
}
