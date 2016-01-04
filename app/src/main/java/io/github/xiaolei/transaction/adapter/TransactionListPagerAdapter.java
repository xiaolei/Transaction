package io.github.xiaolei.transaction.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.Date;

import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.ui.TransactionListFragment;
import io.github.xiaolei.transaction.viewmodel.DateRange;
import io.github.xiaolei.transaction.viewmodel.TransactionFilterType;

/**
 * TODO: add comment
 */
public class TransactionListPagerAdapter extends FragmentStatePagerAdapter {
    public static final String TAG = TransactionListPagerAdapter.class.getSimpleName();

    private Date mStartDate;
    private int mCount;
    private TransactionFilterType mTransactionFilterType = TransactionFilterType.BY_DAY;

    public TransactionListPagerAdapter(FragmentManager fm, Date startDate, Date endDate) {
        super(fm);

        mStartDate = startDate;
        refreshCount();
        Log.d(TAG, "Page count: " + mCount);
    }

    public void changeDateRange(Date startDate, TransactionFilterType transactionFilterType) {
        mStartDate = startDate;
        mTransactionFilterType = transactionFilterType;

        refreshCount();
        notifyDataSetChanged();
    }

    private void refreshCount() {
        switch (mTransactionFilterType) {
            case BY_DAY:
                mCount = 365;
                break;
            case BY_WEEK:
                mCount = 52;
                break;
            case BY_MONTH:
                mCount = 12;
                break;
            case BY_YEAR:
                mCount = 100;
                break;
            default:
                break;
        }
    }

    public DateRange getCurrentDateRange(int position){
        Date startDate = DateTimeUtils.addDays(DateTimeUtils.getStartTimeOfDate(mStartDate), -mCount + 1 + position);
        Date endDate = DateTimeUtils.getEndTimeOfDate(startDate);
        switch (mTransactionFilterType) {
            case BY_DAY:
                startDate = DateTimeUtils.addDays(DateTimeUtils.getStartTimeOfDate(mStartDate), -mCount + 1 + position);
                endDate = DateTimeUtils.getEndTimeOfDate(startDate);
                break;
            case BY_WEEK:
                startDate = DateTimeUtils.getStartDayOfWeek(mStartDate, -mCount + 1 + position);
                endDate = DateTimeUtils.getEndDayOfWeek(startDate);
                break;
            case BY_MONTH:
                startDate = DateTimeUtils.getStartDayOfMonth(mStartDate, -mCount + 1 + position);
                endDate = DateTimeUtils.getEndDayOfMonth(startDate);
                break;
            case BY_YEAR:
                startDate = DateTimeUtils.getStartDayOfYear(mStartDate, -mCount + 1 + position);
                endDate = DateTimeUtils.getEndDayOfYear(startDate);
                break;
            default:
                break;
        }

        return new DateRange(startDate, endDate);
    }

    /*
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
    */

    @Override
    public Fragment getItem(int position) {
        DateRange dateRange = getCurrentDateRange(position);
        return TransactionListFragment.newInstance(dateRange.startDate, dateRange.endDate);
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
