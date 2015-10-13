package io.github.xiaolei.transaction.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.ui.AnalysisFragment;
import io.github.xiaolei.transaction.ui.CalculatorFragment;
import io.github.xiaolei.transaction.ui.DashboardFragment;
import io.github.xiaolei.transaction.ui.ProductsFragment;
import io.github.xiaolei.transaction.ui.TagsFragment;
import io.github.xiaolei.transaction.ui.TransactionListFragment;

/**
 * TODO: add comment
 */
public class FragmentListPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;

    public FragmentListPagerAdapter(FragmentManager fm) {
        super(fm);

        Date transactionDate = new Date();
        mFragments = new ArrayList<Fragment>();
        mFragments.add(CalculatorFragment.newInstance());
        mFragments.add(DashboardFragment.newInstance());
        mFragments.add(TransactionListFragment.newInstance(DateTimeUtils.getStartTimeOfDate(transactionDate),
                DateTimeUtils.getEndTimeOfDate(transactionDate)));
        mFragments.add(AnalysisFragment.newInstance());
        mFragments.add(ProductsFragment.newInstance(false, false));
        mFragments.add(TagsFragment.newInstance());
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }
}
