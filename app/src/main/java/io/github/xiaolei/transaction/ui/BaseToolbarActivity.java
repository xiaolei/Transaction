package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public abstract class BaseToolbarActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_base);
        LinearLayout contentLayout = (LinearLayout) findViewById(R.id.linearLayoutContentContainer);
        getLayoutInflater().inflate(getLayoutId(), contentLayout);

        if (hasActionBar()) {
            setupToolbar(R.id.toolbar, false);
            setTitle(getActionBarTitle());
        } else {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setVisibility(View.GONE);
            }
        }

        initialize();
    }

    @Override
    public void setTitle(CharSequence title) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }

    @Override
    public void setTitle(int titleId) {
        setTitle(getString(titleId));
    }

    protected abstract int getLayoutId();

    protected abstract void initialize();

    protected abstract String getActionBarTitle();

    protected abstract boolean hasActionBar();
}
