package io.github.xiaolei.transaction.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import android.widget.Toast;

import java.io.File;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.enterpriselibrary.utility.PhotoPicker;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.event.PickPhotoEvent;
import io.github.xiaolei.transaction.event.SwitchToFragmentEvent;
import io.github.xiaolei.transaction.listener.OnGotPermissionResultListener;
import io.github.xiaolei.transaction.listener.PermissionResult;

/**
 * TODO: add comment
 */
public abstract class BaseActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    public static final int REQUEST_CODE_CHECK_PERMISSION = 100;
    private static final String TAG = BaseActivity.class.getSimpleName();
    protected OnGotPermissionResultListener mOnGotPermissionResultListener;

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
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

    protected void changeToolbarColor(int color) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setBackgroundColor(color);
        }
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

    public void onEvent(SwitchToFragmentEvent event) {
        switchToFragment(event.fragmentTagName, event.arguments);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK && (requestCode == PhotoPicker.IMAGE_PICK
                || requestCode == PhotoPicker.IMAGE_CAPTURE)) {
            String photoFileName = "";
            switch (requestCode) {
                case PhotoPicker.IMAGE_PICK:
                    photoFileName = PhotoPicker.getInstance(this).extractImageUrlFromGallery(this, data);
                    Toast.makeText(this, photoFileName, Toast.LENGTH_SHORT).show();
                    break;

                case PhotoPicker.IMAGE_CAPTURE:
                    photoFileName = PhotoPicker.getInstance(this).getCameraPhotoFileName();
                    Toast.makeText(this, photoFileName, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }

            if (!TextUtils.isEmpty(photoFileName)) {
                Logger.d(TAG, String.format("photoFileName: %s", photoFileName));
                EventBus.getDefault().post(new PickPhotoEvent(photoFileName));
            }
        }
    }

    protected void onGotPermissionResult(PermissionResult permissionResult) {
        if (permissionResult != null && mOnGotPermissionResultListener != null) {
            mOnGotPermissionResultListener.onGotPermissionResult(permissionResult);
        }
    }

    public void checkCameraPermission(final OnGotPermissionResultListener onGotPermissionResultListener) {
        checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, REQUEST_CODE_CHECK_PERMISSION, new OnGotPermissionResultListener() {
            @Override
            public void onGotPermissionResult(PermissionResult permissionResult) {
                if (!permissionResult.granted) {
                    if (onGotPermissionResultListener != null) {
                        onGotPermissionResultListener.onGotPermissionResult(permissionResult);
                    }
                } else {
                    checkPermission(Manifest.permission.CAMERA,
                            REQUEST_CODE_CHECK_PERMISSION,
                            onGotPermissionResultListener);
                }
            }
        });
    }

    public void checkPermission(String permission, int requestCode, OnGotPermissionResultListener onGotPermissionResultListener) {
        mOnGotPermissionResultListener = onGotPermissionResultListener;

        if (ActivityCompat.checkSelfPermission(this, permission)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{permission},
                    requestCode);
        } else {
            onGotPermissionResult(new PermissionResult(permission, true));
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == REQUEST_CODE_CHECK_PERMISSION) {
            boolean granted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            onGotPermissionResult(new PermissionResult(permissions[0], granted));
        }
    }
}
