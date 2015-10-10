package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.gson.Gson;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.FragmentListPagerAdapter;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.event.AccountInfoLoadCompletedEvent;
import io.github.xiaolei.transaction.event.AppInitCompletedEvent;
import io.github.xiaolei.transaction.event.PickPhotoEvent;
import io.github.xiaolei.transaction.util.PhotoPicker;
import io.github.xiaolei.transaction.widget.AccountView;
import me.tabak.fragmentswitcher.FragmentSwitcher;


public class MainActivity extends BaseActivity
        implements ITitleChangeable, IFragmentSwitchable, NavigationView.OnNavigationItemSelectedListener {
    public static final int VIEW_INDEX_SPLASH = 0;
    public static final int VIEW_INDEX_CONTENT = 1;
    private CharSequence mTitle;
    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventBus.getDefault().register(this);
        mViewHolder = new ViewHolder(this);
        mTitle = getTitle();

        start();
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
        mViewHolder.navigationView.setNavigationItemSelectedListener(this);
        setupToolbar(mViewHolder.drawerLayout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mViewHolder.drawerLayout, mViewHolder.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mViewHolder.drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        FragmentListPagerAdapter adapter = new FragmentListPagerAdapter(getSupportFragmentManager());
        mViewHolder.fragmentSwitcher.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        boolean goAhead = true;
        Fragment fragment = mViewHolder.fragmentSwitcher.getCurrentFragment();
        if (fragment != null) {
            if (fragment instanceof CalculatorFragment) {
                CalculatorFragment calculatorFragment = (CalculatorFragment) fragment;
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
    public FragmentSwitcher getFragmentSwitcher() {
        return mViewHolder.fragmentSwitcher;
    }

    @Override
    public void switchToProductEditor(final long productId) {
        Bundle args = new Bundle();
        args.putLong(ProductEditorFragment.ARG_PRODUCT_ID, productId);

    }

    @Override
    public void switchToTagEditor(final long tagId) {
        Bundle args = new Bundle();
        args.putLong(TagEditorFragment.ARG_TAG_ID, tagId);

    }

    @Override
    public void switchToTagList() {
        getFragmentSwitcher().setCurrentItem(2);
    }

    @Override
    public void switchToProductList(final boolean reload, final boolean isSelectionMode) {
        Bundle args = new Bundle();
        args.putBoolean(QuickProductsFragment.ARG_IS_SELECTION_MODE, isSelectionMode);

        getFragmentSwitcher().setCurrentItem(1);
    }

    @Override
    public void switchToCalculator(final Product product) {
        Bundle args = new Bundle();
        args.putString(CalculatorFragment.ARG_PRODUCT, new Gson().toJson(product));
        getFragmentSwitcher().setCurrentItem(0);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void setTitle(String title) {
        this.getSupportActionBar().setTitle(title);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getOrder() == 2) {
            Intent intent = new Intent(this, TransactionListActivity.class);
            startActivity(intent);
        } else {
            getFragmentSwitcher().setCurrentItem(menuItem.getOrder());
        }

        mViewHolder.drawerLayout.closeDrawers();
        return true;
    }

    public void onEvent(AccountInfoLoadCompletedEvent event) {
        showAccountInfo();
    }

    public void onEvent(AppInitCompletedEvent event) {
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
    }

    private class ViewHolder {
        public FragmentSwitcher fragmentSwitcher;
        public NavigationView navigationView;
        public DrawerLayout drawerLayout;
        public AccountView accountView;
        public ViewFlipper mainViewFlipper;
        public Toolbar toolbar;

        public ViewHolder(Activity activity) {
            fragmentSwitcher = (FragmentSwitcher) activity.findViewById(R.id.fragmentSwitcher);
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
