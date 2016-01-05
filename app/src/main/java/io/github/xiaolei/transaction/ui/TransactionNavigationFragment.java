package io.github.xiaolei.transaction.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.TransactionListPagerAdapter;
import io.github.xiaolei.transaction.event.FinishActionMode;
import io.github.xiaolei.transaction.util.ActivityHelper;
import io.github.xiaolei.transaction.viewmodel.DateRange;
import io.github.xiaolei.transaction.viewmodel.TransactionFilterType;

/**
 * TODO: add comment
 */
public class TransactionNavigationFragment extends BaseFragment {
    public static final String TAG = TransactionNavigationFragment.class.getSimpleName();
    public static final String ARG_TRANSACTION_DATE = "arg_transaction_date";

    private ViewHolder mViewHolder;
    private Date mTransactionDate = DateTimeUtils.getStartTimeOfDate(new Date());
    private TransactionListPagerAdapter mTransactionListPagerAdapter;
    private TransactionFilterType mFilterType = TransactionFilterType.BY_DAY;
    private int mCheckedMenuItemId = R.id.action_by_day;

    public static TransactionNavigationFragment newInstance(Date transactionDate) {
        TransactionNavigationFragment result = new TransactionNavigationFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRANSACTION_DATE, transactionDate.getTime());
        return result;
    }

    public TransactionNavigationFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        // Show add action button, only when filter type is by day.
        menu.getItem(0).setVisible(mFilterType == TransactionFilterType.BY_DAY);

        MenuItem checkedMenuItem = menu.findItem(mCheckedMenuItemId);
        if (checkedMenuItem != null) {
            checkedMenuItem.setChecked(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_transactions_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (!isAdded()) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_new_transaction:
                if (mFilterType == TransactionFilterType.BY_DAY) {
                    DateRange currentDateRange = mTransactionListPagerAdapter.getCurrentDateRange(mViewHolder.viewPagerFragmentList.getCurrentItem());
                    ActivityHelper.startNewTransactionActivity(getActivity(), DateTimeUtils.replaceWithCurrentTime(currentDateRange.startDate));
                }
                return true;
            case R.id.action_by_day:
            case R.id.action_by_week:
            case R.id.action_by_month:
            case R.id.action_by_year:
                mCheckedMenuItemId = item.getItemId();

                switch (item.getItemId()) {
                    case R.id.action_by_day:
                        query(TransactionFilterType.BY_DAY, mTransactionDate);
                        return true;
                    case R.id.action_by_week:
                        query(TransactionFilterType.BY_WEEK, mTransactionDate);
                        return true;
                    case R.id.action_by_month:
                        query(TransactionFilterType.BY_MONTH, mTransactionDate);
                        return true;
                    case R.id.action_by_year:
                        query(TransactionFilterType.BY_YEAR, mTransactionDate);
                        return true;
                    default:
                        break;
                }
            default:
                return super.onOptionsItemSelected(item);
        }
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
        mViewHolder.viewPagerFragmentList.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                EventBus.getDefault().post(new FinishActionMode());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void load() {
        mTransactionListPagerAdapter = new TransactionListPagerAdapter(getActivity().getSupportFragmentManager(), mTransactionDate, mTransactionDate);
        mViewHolder.viewPagerFragmentList.setAdapter(mTransactionListPagerAdapter);
        mViewHolder.viewPagerFragmentList.setCurrentItem(Math.max(0, mTransactionListPagerAdapter.getCount() - 1), false);
    }

    private void query(TransactionFilterType transactionFilterType, Date date) {
        mFilterType = transactionFilterType;
        getAttachedActivity(MainActivity.class).supportInvalidateOptionsMenu();

        mTransactionListPagerAdapter.changeDateRange(date, transactionFilterType);

        if (mTransactionListPagerAdapter.getCount() > 0) {
            mViewHolder.viewPagerFragmentList.setCurrentItem(Math.max(0, mTransactionListPagerAdapter.getCount() - 1), false);
        }
    }

    @Override
    public int getActionBarTitle() {
        return R.string.empty;
    }

    private class ViewHolder {
        public ViewPager viewPagerFragmentList;

        public ViewHolder(View view) {
            viewPagerFragmentList = (ViewPager) view.findViewById(R.id.viewPagerFragmentList);
        }
    }
}
