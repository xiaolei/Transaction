package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;

/**
 * Creates new transaction
 */
public class NewTransactionActivity extends BaseActivity {
    public static final String ARG_TRANSACTION_DATE = "arg_transaction_date";

    private Date mTransactionDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_transaction);

        initialize();
    }

    private void initialize() {
        long date = getIntent().getLongExtra(ARG_TRANSACTION_DATE, -1);
        if(date > 0){
            mTransactionDate = new Date(date);
        }

        setupToolbar(false);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, CalculatorFragment.newInstance(mTransactionDate))
                .commit();
    }

    public void onEvent(RefreshTransactionListEvent event) {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
