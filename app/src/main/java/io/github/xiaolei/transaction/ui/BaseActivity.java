package io.github.xiaolei.transaction.ui;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public abstract class BaseActivity extends AppCompatActivity {
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

    protected Toolbar setupToolbar(boolean showMenuIcon) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Show menu icon
            final ActionBar ab = getSupportActionBar();
            if(showMenuIcon) {
                ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }

        return toolbar;
    }

    protected Toolbar setupToolbar(int toolbarResourceId, boolean showMenuIcon) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarResourceId);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            // Show menu icon
            final ActionBar ab = getSupportActionBar();
            if(showMenuIcon) {
                ab.setHomeAsUpIndicator(R.drawable.ic_menu);
            }
            ab.setHomeButtonEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
        }

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
}
