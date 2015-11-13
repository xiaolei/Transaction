package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public abstract class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager manager = getSupportFragmentManager();
                if (manager != null) {
                    int backStackEntryCount = manager.getBackStackEntryCount();
                    if (backStackEntryCount == 0) {
                        finish();
                        return;
                    }

                    BaseFragment fragment = (BaseFragment) manager.getFragments().get(backStackEntryCount - 1);
                    if (fragment != null) {
                        fragment.onPoppedFromBackStack();
                        onFragmentPoppedFromBackStack(fragment);
                    }
                }
            }
        });
    }

    protected void onFragmentPoppedFromBackStack(BaseFragment fragment) {

    }

    protected int getFragmentContainerId() {
        return R.id.fragmentContainer;
    }

    protected Toolbar setupToolbar(final DrawerLayout drawerLayout) {
        Toolbar toolbar = setupToolbar(true);
        if (toolbar != null && drawerLayout != null) {
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!drawerLayout.isDrawerOpen(Gravity.LEFT)) {
                        drawerLayout.openDrawer(Gravity.LEFT);
                    } else {
                        drawerLayout.closeDrawers();
                    }
                }
            });
        }

        return toolbar;
    }

    protected void setupToolbar(Toolbar toolbar, boolean showMenuIcon) {
        if (toolbar == null) {
            return;
        }

        ActionBar existingActionBar = getSupportActionBar();
        if (existingActionBar != null) {
            existingActionBar.hide();
        }

        toolbar.setVisibility(View.VISIBLE);
        setSupportActionBar(toolbar);
        // Show menu icon
        final ActionBar ab = getSupportActionBar();
        if (showMenuIcon) {
            ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        }

        ab.setHomeButtonEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
    }

    protected Toolbar setupToolbar(boolean showMenuIcon) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupToolbar(toolbar, showMenuIcon);

        return toolbar;
    }

    protected Toolbar setupToolbar(int toolbarResourceId, boolean showMenuIcon) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarResourceId);
        setupToolbar(toolbar, showMenuIcon);

        return toolbar;
    }

    protected Toolbar setupToolbar(View.OnClickListener onNavigationButtonClickListener) {
        Toolbar toolbar = setupToolbar(false);
        if (toolbar != null && onNavigationButtonClickListener != null) {
            toolbar.setNavigationOnClickListener(onNavigationButtonClickListener);
        }

        return toolbar;
    }

    protected Toolbar setupToolbar(int navigationIconResourceId, View.OnClickListener onNavigationButtonClickListener) {
        Toolbar toolbar = setupToolbar(onNavigationButtonClickListener);
        if (toolbar != null) {
            toolbar.setNavigationIcon(navigationIconResourceId);
        }

        return toolbar;
    }

    protected Fragment getCurrentFragment() {
        int fragmentContainerId = getFragmentContainerId();
        Fragment fragment = getSupportFragmentManager().findFragmentById(fragmentContainerId);
        return fragment;
    }

    protected <T extends BaseFragment> void switchToFragment(Class<T> fragmentType, Bundle arguments) {
        String fragmentTagName = fragmentType.getName();

        switchToFragment(fragmentTagName, arguments);
    }

    protected void switchToFragment(String fragmentTagName, Bundle arguments) {
        if (TextUtils.isEmpty(fragmentTagName)) {
            return;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        int fragmentContainerId = getFragmentContainerId();

        Fragment currentFragment = fragmentManager.findFragmentById(fragmentContainerId);
        if (currentFragment != null && fragmentTagName.equalsIgnoreCase(currentFragment.getClass().getName())) {
            return;
        }

        if (containsInFragmentBackStack(fragmentTagName)) {
            fragmentManager.popBackStack(fragmentTagName, 0);
            return;
        }

        Fragment fragment = Fragment.instantiate(this, fragmentTagName, arguments);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.addToBackStack(fragmentTagName);
        transaction.replace(fragmentContainerId, fragment).commit();
    }

    private boolean containsInFragmentBackStack(String fragmentTagName) {
        if (TextUtils.isEmpty(fragmentTagName)) {
            return false;
        }

        FragmentManager fragmentManager = getSupportFragmentManager();

        for (int i = 0; i < fragmentManager.getBackStackEntryCount(); i++) {
            FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(0);
            if (fragmentTagName.equalsIgnoreCase(backStackEntry.getName())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
