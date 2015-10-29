package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Date;

import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.TransactionListPagerAdapter;
import io.github.xiaolei.transaction.adapter.TransactionNavigatorAdapter;
import io.github.xiaolei.transaction.viewmodel.TransactionFilterType;
import io.github.xiaolei.transaction.viewmodel.TransactionNavigatorItem;

/**
 * TODO: add comment
 */
public class TransactionNavigationFragment extends BaseFragment {
    public static final String TAG = TransactionNavigationFragment.class.getSimpleName();
    public static final String ARG_TRANSACTION_DATE = "arg_transaction_date";

    private ViewHolder mViewHolder;
    private Date mTransactionDate = DateTimeUtils.getStartTimeOfDate(new Date());
    private TransactionNavigatorAdapter mSpinnerAdapter;
    private TransactionListPagerAdapter mTransactionListPagerAdapter;

    public static TransactionNavigationFragment newInstance(Date transactionDate) {
        TransactionNavigationFragment result = new TransactionNavigationFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRANSACTION_DATE, transactionDate.getTime());
        return result;
    }

    @Override
    public boolean useDefaultActionBar() {
        return false;
    }

    @Override
    public int getContentView() {
        return R.layout.activity_transaction_list;
    }

    @Override
    public void initialize(View view) {
        Bundle args = getArguments();
        if (args != null) {
            long value = args.getLong(ARG_TRANSACTION_DATE, -1);
            if (value > 0) {
                mTransactionDate = new Date(value);
            }
        }

        mViewHolder = new ViewHolder(view);
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

        supportNavigationDrawer(mViewHolder.toolbar);
    }

    @Override
    public void load() {
        setupTransactionNavigator();

        mTransactionListPagerAdapter = new TransactionListPagerAdapter(getActivity().getSupportFragmentManager(), mTransactionDate, mTransactionDate);
        mViewHolder.viewPagerFragmentList.setAdapter(mTransactionListPagerAdapter);
    }

    private void query(TransactionFilterType transactionFilterType, Date date) {
        mTransactionListPagerAdapter.changeDateRange(date, transactionFilterType);

        if (mTransactionListPagerAdapter.getCount() > 0) {
            mViewHolder.viewPagerFragmentList.setCurrentItem(Math.max(0, mTransactionListPagerAdapter.getCount() - 1), false);
        }
    }

    private void setupTransactionNavigator() {
        ArrayList<TransactionNavigatorItem> items = new ArrayList<TransactionNavigatorItem>();
        items.add(new TransactionNavigatorItem(TransactionFilterType.TODAY,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_by_day));
        items.add(new TransactionNavigatorItem(TransactionFilterType.THIS_WEEK,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_by_week));
        items.add(new TransactionNavigatorItem(TransactionFilterType.THIS_MONTH,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_by_month));
        items.add(new TransactionNavigatorItem(TransactionFilterType.THIS_YEAR,
                R.drawable.ic_calendar_black_24dp, R.string.transaction_navigator_by_year));

        mSpinnerAdapter = new TransactionNavigatorAdapter(getActivity(), items, mTransactionDate);
        mViewHolder.spinnerTransactionNavigator.setAdapter(mSpinnerAdapter);
    }

    @Override
    public int getActionBarTitle() {
        return R.string.transactions;
    }

    private class ViewHolder {
        public Toolbar toolbar;
        public Spinner spinnerTransactionNavigator;
        public ViewPager viewPagerFragmentList;

        public ViewHolder(View view) {
            toolbar = (Toolbar) view.findViewById(R.id.toolbar);
            spinnerTransactionNavigator = (Spinner) view.findViewById(R.id.spinnerTransactionNavigator);
            viewPagerFragmentList = (ViewPager) view.findViewById(R.id.viewPagerFragmentList);
        }
    }
}
