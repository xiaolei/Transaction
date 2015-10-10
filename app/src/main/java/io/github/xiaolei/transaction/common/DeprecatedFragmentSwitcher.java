package io.github.xiaolei.transaction.common;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import java.util.List;

import io.github.xiaolei.transaction.ui.BaseDataFragment;

/**
 * A help class to help to switch fragments.
 */
@Deprecated
public class DeprecatedFragmentSwitcher {
    private static final String TAG = DeprecatedFragmentSwitcher.class.getSimpleName();
    private Context mContext;
    private FragmentManager mFragmentManager;
    private int mFragmentContainerLayoutResourceId;
    private boolean mAddToBackStack;

    public DeprecatedFragmentSwitcher(Context context, FragmentManager fragmentManager, int fragmentContainerLayoutResourceId, boolean addToBackStack) {
        if (fragmentContainerLayoutResourceId <= 0) {
            throw new IllegalArgumentException("fragmentContainerLayoutResourceId is invalid.");
        }

        if (context == null) {
            throw new IllegalArgumentException("mContext cannot be null.");
        }

        if (fragmentManager == null) {
            throw new IllegalArgumentException("fragmentManager cannot be null.");
        }

        mContext = context;
        mFragmentManager = fragmentManager;
        mFragmentContainerLayoutResourceId = fragmentContainerLayoutResourceId;
        mAddToBackStack = addToBackStack;
    }

    private void hideVisibleFragment(FragmentTransaction ft) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }

        for (Fragment fragment : fragments) {
            if (fragment != null && fragment.isVisible() && fragment instanceof BaseDataFragment) {
                ft.hide(fragment);
            }
        }
    }

    public Fragment getCurrentVisibleFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return null;
        }

        for (Fragment fragment : fragments) {
            if (fragment.isVisible()) return fragment;
        }

        return null;
    }

    public <T extends Fragment> T switchFragment(Class<T> fragmentClass,
                                                 Bundle args) {
        return switchFragment(fragmentClass, args, null);
    }

    public <T extends Fragment> T switchFragment(Class<T> fragmentClass) {
        return switchFragment(fragmentClass, null, null);
    }

    public <T extends Fragment> T switchFragment(Class<T> fragmentClass, Func<T> callFuncIfFragmentAlreadyExists) {
        return switchFragment(fragmentClass, null, callFuncIfFragmentAlreadyExists);
    }

    private void refreshActivityTitle(Fragment fragment) {
        if (fragment == null) {
            return;
        }

        if (fragment instanceof BaseDataFragment) {
            ((BaseDataFragment) fragment).refreshActivityTitle();
        }
    }

    public void refreshActivityTitle() {
        Fragment fragment = getCurrentVisibleFragment();
        refreshActivityTitle(fragment);
    }

    public <T extends Fragment> T switchFragment(Class<T> fragmentClass,
                                                 Bundle args, Func<T> callFuncIfFragmentAlreadyExists) {
        String fragmentClassName = fragmentClass.getName();
        Log.d(TAG, "Fragment class Name: " + fragmentClassName);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(fragmentClassName);

        if (fragment != null) {
            T returnFragment = fragmentClass.cast(fragment);
            if (fragment.isVisible()) {
                Log.d(TAG, "[Already shown] " + fragmentClassName);

                if (callFuncIfFragmentAlreadyExists != null) {
                    callFuncIfFragmentAlreadyExists.call(returnFragment);
                }

                refreshActivityTitle(fragment);

                return returnFragment;
            } else {
                if (mAddToBackStack) {
                    if (manager.popBackStackImmediate(fragmentClassName, 0)) {
                        refreshActivityTitle(fragment);

                        if (callFuncIfFragmentAlreadyExists != null) {
                            callFuncIfFragmentAlreadyExists.call(returnFragment);
                        }

                        return returnFragment;
                    }
                }

                if (fragment.isAdded()) {
                    Log.d(TAG, fragmentClassName + " - show.");
                    hideVisibleFragment(ft);
                    ft.show(fragment);

                    refreshActivityTitle(fragment);
                    if (callFuncIfFragmentAlreadyExists != null) {
                        callFuncIfFragmentAlreadyExists.call(returnFragment);
                    }

                    return returnFragment;
                }
            }
        }


        hideVisibleFragment(ft);
        Log.d(TAG, String.format("[Fragment] - %s - Created",
                fragmentClassName));
        fragment = Fragment.instantiate(mContext, fragmentClassName, args);
        Log.d(TAG, fragmentClassName + " - add.");
        ft.add(mFragmentContainerLayoutResourceId, fragment, fragmentClassName);

        if (mAddToBackStack) {
            ft.addToBackStack(fragmentClassName);
        }

        ft.commit();

        return fragmentClass.cast(fragment);
    }

    public <T extends Fragment> T switchFragment(Class<T> fragmentClass,
                                                 Bundle args, Func<T> callFuncIfFragmentAlreadyExists, boolean newMode) {
        String fragmentClassName = fragmentClass.getName();
        Log.d(TAG, "Fragment class Name: " + fragmentClassName);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(fragmentClassName);

        if (fragment != null) {
            T returnFragment = fragmentClass.cast(fragment);
            if (fragment.isVisible()) {
                Log.d(TAG, "[Already shown] " + fragmentClassName);

                if (callFuncIfFragmentAlreadyExists != null) {
                    callFuncIfFragmentAlreadyExists.call(returnFragment);
                }

                refreshActivityTitle(fragment);

                return returnFragment;
            } else {
                if (mAddToBackStack) {
                    if (manager.popBackStackImmediate(fragmentClassName, 0)) {
                        refreshActivityTitle(fragment);

                        if (callFuncIfFragmentAlreadyExists != null) {
                            callFuncIfFragmentAlreadyExists.call(returnFragment);
                        }

                        return returnFragment;
                    }
                }
            }
        }

        Log.d(TAG, String.format("[Fragment] - %s - Created",
                fragmentClassName));
        fragment = Fragment.instantiate(mContext, fragmentClassName, args);
        Log.d(TAG, fragmentClassName + " - add.");
        ft.add(mFragmentContainerLayoutResourceId, fragment, fragmentClassName);

        if (mAddToBackStack) {
            ft.addToBackStack(fragmentClassName);
        }

        ft.commit();

        return fragmentClass.cast(fragment);
    }

    private void hideAllFragments(FragmentTransaction ft) {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments == null) {
            return;
        }

        for (Fragment fragment : fragments) {
            ft.hide(fragment);
        }
    }

    private FragmentManager getSupportFragmentManager() {
        return mFragmentManager;
    }

    public interface Func<T> {
        void call(T source);
    }
}
