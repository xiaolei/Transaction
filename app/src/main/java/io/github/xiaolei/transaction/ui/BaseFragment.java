package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.event.NavigationDrawerStateEvent;

/**
 * TODO: add comment
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    public abstract int getContentView();

    public abstract void initialize(View view);

    public abstract void load();

    public abstract int getActionBarTitle();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(getContentView(), container, false);
        initialize(view);
        setHasOptionsMenu(true);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (getView() != null) {
            onFragmentShown();
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        Logger.d(TAG, "onViewCreated");
        super.onViewCreated(view, savedInstanceState);

        load();
    }

    @Override
    public void onResume() {
        super.onResume();

        setActionBarTitle(getString(getActionBarTitle()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    protected <T extends BaseFragment> void switchToFragment(Class<T> fragmentType) {
        MainActivity activity = getAttachedActivity(MainActivity.class);
        if (activity != null) {
            activity.switchToFragment(fragmentType, null);
        }
    }

    protected <T extends Activity> T getAttachedActivity(Class<T> activityType) {
        Activity activity = getActivity();
        if (activity == null || !activityType.isInstance(activity)) {
            return null;
        }

        return (T) getActivity();
    }

    protected void setNavigationDrawerVisibility(boolean visible) {
        EventBus.getDefault().post(new NavigationDrawerStateEvent(visible));
    }

    protected void openDrawers() {
        setNavigationDrawerVisibility(true);
    }

    protected void closeDrawers() {
        setNavigationDrawerVisibility(false);
    }

    /**
     * Supports to show a navigation menu button on the left of specified toolbar.
     * When click it, show the navigation drawer.
     */
    protected void supportNavigationDrawer(Toolbar toolbar) {
        if (!isAdded() || toolbar == null) {
            return;
        }

        toolbar.setNavigationIcon(R.drawable.ic_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDrawers();
            }
        });
    }

    /**
     * Every time when the fragment is shown, this method will be invoked.
     */
    protected void onFragmentShown() {
    }

    public void refreshActivityTitle() {
        if (!isAdded()) {
            return;
        }

        Activity activity = getActivity();
        if (activity != null && activity instanceof ITitleChangeable) {
            ((ITitleChangeable) activity).setActionBarTitle(getActionBarTitle());
        }
    }

    public void setActionBarTitle(String title) {
        if (!isAdded()) {
            return;
        }

        Activity activity = getActivity();
        if (activity != null && activity instanceof ITitleChangeable) {
            ((ITitleChangeable) activity).setActionBarTitle(getActionBarTitle());
        }
    }

    public void showSnackbarMessage(View view, String message) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    public void showSnackbarMessage(View view, String message, final View.OnClickListener onClickListener) {
        final Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                snackbar.dismiss();
                if (onClickListener != null) {
                    onClickListener.onClick(view);
                }
            }
        });
        snackbar.show();
    }

    protected void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void onPoppedFromBackStack() {

    }
}
