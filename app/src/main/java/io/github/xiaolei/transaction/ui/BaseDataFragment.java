package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * TODO: add comment
 */
public abstract class BaseDataFragment extends Fragment {

    public abstract String getActionBarTitle();

    public abstract void switchToBusyView();

    public abstract void switchToRetryView();

    public abstract void switchToDataView();

    @Override
    public void onResume() {
        super.onResume();
        refreshActivityTitle();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    public void refreshActivityTitle() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof ITitleChangeable) {
            ((ITitleChangeable) activity).setTitle(getActionBarTitle());
        }
    }

    public void setActionBarTitle(String title) {
        Activity activity = getActivity();
        if (activity != null && activity instanceof ITitleChangeable) {
            ((ITitleChangeable) activity).setTitle(getActionBarTitle());
        }
    }

    public IFragmentSwitchable getFragmentSwitcher() {
        Activity activity = getActivity();
        if (activity != null && activity instanceof IFragmentSwitchable) {
            return (IFragmentSwitchable) activity;
        }

        return null;
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
}
