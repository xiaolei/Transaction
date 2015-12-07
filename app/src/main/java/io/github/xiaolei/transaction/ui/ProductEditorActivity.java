package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import io.github.xiaolei.transaction.R;

public class ProductEditorActivity extends BaseActivity {
    private ProductEditorFragment mFragment;
    public static final String ARG_PRODUCT_ID = "product_id";
    private long mProductId;
    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_editor);

        initialize();
        handleIntent(getIntent());
    }

    protected void initialize() {
        mViewHolder = new ViewHolder(this);
        mFragment = (ProductEditorFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentProductEditor);
        setupToolbar(R.drawable.ic_arrow_back_white_24dp, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        Bundle args = intent.getExtras();
        if (args != null) {
            mProductId = args.getLong(ARG_PRODUCT_ID, -1);
            mFragment.setProductId(mProductId);
            mFragment.loadAsync(mProductId);
        }
    }

    private class ViewHolder {

        public ViewHolder(Activity activity) {

        }
    }
}
