package io.github.xiaolei.transaction.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;

import java.util.Date;

import io.github.xiaolei.transaction.ui.TransactionListFragment;
import io.github.xiaolei.transaction.util.DateTimeUtils;

/**
 * TODO: add comment
 */
public class TransactionListPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = TransactionListPagerAdapter.class.getSimpleName();
    private Date mStartDate;
    private Date mEndDate;
    private int mCount;

    public TransactionListPagerAdapter(FragmentManager fm, Date startDate, Date endDate) {
        super(fm);

        mStartDate = startDate;
        mEndDate = endDate;
        mCount = (int) (DateTimeUtils.betweenDays(DateTimeUtils.parseDate("1970-1-1"), endDate));
        Log.d(TAG, "betweenDays: " + mCount);
    }

    public void changeDateRange(Date startDate, Date endDate) {
        mStartDate = startDate;
        mEndDate = endDate;
        notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Date startDate = DateTimeUtils.addDays(mStartDate, position);
        return TransactionListFragment.newInstance(startDate, mEndDate);
    }

    @Override
    public int getCount() {
        return mCount;
    }
}
