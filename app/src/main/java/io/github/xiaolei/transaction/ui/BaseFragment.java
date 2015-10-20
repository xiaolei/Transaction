package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

/**
 * TODO: add comment
 */
public abstract class BaseFragment extends Fragment {
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

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        load();
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshActivityTitle();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    /**
     * Indicates whether show the default action bar. If not,
     * fragment layout should contains its own action bar.
     *
     * @return
     */
    public boolean useDefaultActionBar() {
        return true;
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

    protected void hideSoftKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow((null == getActivity().getCurrentFocus()) ? null : getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
