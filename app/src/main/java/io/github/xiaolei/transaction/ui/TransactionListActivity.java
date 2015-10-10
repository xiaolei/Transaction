package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.TransactionListPagerAdapter;
import io.github.xiaolei.transaction.adapter.TransactionNavigatorAdapter;
import io.github.xiaolei.transaction.util.DateTimeUtils;
import io.github.xiaolei.transaction.viewmodel.TransactionFilterType;
import io.github.xiaolei.transaction.viewmodel.TransactionNavigatorItem;

public class TransactionListActivity extends BaseActivity {
    private ViewHolder mViewHolder;
    private Date mTransactionDate = DateTimeUtils.getStartTimeOfDate(new Date());
    private TransactionNavigatorAdapter mSpinnerAdapter;
    private TransactionListPagerAdapter mTransactionListPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_list);

        initialize();
    }

    private void initialize() {
        mViewHolder = new ViewHolder(this);
        setupToolbar(false);
        setTitle("");
        mViewHolder.spinnerTransactionNavigator.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                TransactionNavigatorItem selectedItem = (TransactionNavigatorItem) adapterView.getItemAtPosition(position);
                mSpinnerAdapter.setSelectedItem(selectedItem.transactionFilterType);
                query(selectedItem.transactionFilterType, mTransactionDate);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setupTransactionNavigator();

        mTransactionListPagerAdapter = new TransactionListPagerAdapter(getSupportFragmentManager(), mTransactionDate, mTransactionDate);
        mViewHolder.viewPagerFragmentList.setAdapter(mTransactionListPagerAdapter);
        if(mTransactionListPagerAdapter.getCount() > 0) {
            mViewHolder.viewPagerFragmentList.setCurrentItem(mTransactionListPagerAdapter.getCount() - 1);
        }
    }

    private void query(int transactionFilterType, Date date) {
        switch (transactionFilterType) {
            case TransactionFilterType.TODAY:
                mTransactionListPagerAdapter.changeDateRange(DateTimeUtils.getStartTimeOfDate(date),
                        DateTimeUtils.getEndTimeOfDate(date));
                break;
            case TransactionFilterType.THIS_WEEK:
                mTransactionListPagerAdapter.changeDateRange(DateTimeUtils.getStartDayOfWeek(date),
                        DateTimeUtils.getEndDayOfWeek(date));
                break;
            case TransactionFilterType.THIS_MONTH:
                mTransactionListPagerAdapter.changeDateRange(DateTimeUtils.getStartDayOfMonth(date),
                        DateTimeUtils.getEndDayOfMonth(date));
                break;
            case TransactionFilterType.THIS_YEAR:
                mTransactionListPagerAdapter.changeDateRange(DateTimeUtils.getStartDayOfYear(date),
                        DateTimeUtils.getEndDayOfYear(date));
                break;
            default:
                break;
        }
    }

    private void setupTransactionNavigator() {
        ArrayList<TransactionNavigatorItem> items = new ArrayList<TransactionNavigatorItem>();
        items.add(new TransactionNavigatorItem(TransactionFilterType.TODAY,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_today));
        items.add(new TransactionNavigatorItem(TransactionFilterType.THIS_WEEK,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_this_week));
        items.add(new TransactionNavigatorItem(TransactionFilterType.THIS_MONTH,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_this_month));
        items.add(new TransactionNavigatorItem(TransactionFilterType.THIS_YEAR,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_this_year));

        mSpinnerAdapter = new TransactionNavigatorAdapter(this, items, mTransactionDate);
        mViewHolder.spinnerTransactionNavigator.setAdapter(mSpinnerAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transaction_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class ViewHolder {
        public Spinner spinnerTransactionNavigator;
        public ViewPager viewPagerFragmentList;

        public ViewHolder(Activity activity) {
            spinnerTransactionNavigator = (Spinner) activity.findViewById(R.id.spinnerTransactionNavigator);
            viewPagerFragmentList = (ViewPager) activity.findViewById(R.id.viewPagerFragmentList);
        }
    }
}
