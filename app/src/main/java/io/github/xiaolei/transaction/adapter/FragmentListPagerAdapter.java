package io.github.xiaolei.transaction.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.transaction.ui.AnalysisFragment;
import io.github.xiaolei.transaction.ui.BaseFragment;
import io.github.xiaolei.transaction.ui.CalculatorFragment;
import io.github.xiaolei.transaction.ui.DashboardFragment;
import io.github.xiaolei.transaction.ui.ProductListFragment;
import io.github.xiaolei.transaction.ui.TagListFragment;
import io.github.xiaolei.transaction.ui.TransactionNavigationFragment;

/**
 * TODO: add comment
 */
public class FragmentListPagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> mFragments;

    public FragmentListPagerAdapter(FragmentManager fm) {
        super(fm);

        Date transactionDate = new Date();
        mFragments = new ArrayList<BaseFragment>();
        mFragments.add(CalculatorFragment.newInstance(new Date()));
        mFragments.add(DashboardFragment.newInstance());
        mFragments.add(TransactionNavigationFragment.newInstance(DateTimeUtils.getStartTimeOfDate(transactionDate)));
        mFragments.add(AnalysisFragment.newInstance());
        mFragments.add(ProductListFragment.newInstance(false, false));
        mFragments.add(TagListFragment.newInstance());
    }

    public List<BaseFragment> getAllFragments() {
        return mFragments;
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
