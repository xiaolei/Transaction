package io.github.xiaolei.transaction.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import io.github.xiaolei.transaction.listener.OnCalculatorActionClickListener;
import io.github.xiaolei.transaction.listener.OnCalculatorActionLongClickListener;
import io.github.xiaolei.transaction.listener.OnProductSelectedListener;
import io.github.xiaolei.transaction.ui.PriceFragment;
import io.github.xiaolei.transaction.ui.QuickProductsFragment;
import io.github.xiaolei.transaction.widget.CalculatorOutputView;

/**
 * TODO: add comment
 */
public class CalculatorPagerAdapter extends FragmentPagerAdapter {
    private CalculatorOutputView mOutputView;
    private OnCalculatorActionClickListener mOnCalculatorActionClickListener;
    private OnProductSelectedListener mOnProductSelectedListener;
    private OnCalculatorActionLongClickListener mOnCalculatorActionLongClickListener;

    public CalculatorPagerAdapter(FragmentManager fm, CalculatorOutputView outputView, OnCalculatorActionClickListener onCalculatorActionClickListener, OnProductSelectedListener onProductSelectedListener
    , OnCalculatorActionLongClickListener onCalculatorActionLongClickListener) {
        super(fm);

        mOutputView = outputView;
        mOnCalculatorActionClickListener = onCalculatorActionClickListener;
        mOnProductSelectedListener = onProductSelectedListener;
        mOnCalculatorActionLongClickListener = onCalculatorActionLongClickListener;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment result = null;
        switch (position) {
            case 0:
                QuickProductsFragment productsFragment = QuickProductsFragment.newInstance(true, true);
                productsFragment.setIsSelectionMode(true);
                productsFragment.setOnProductSelectedListener(mOnProductSelectedListener);
                result = productsFragment;
                break;
            case 1:
                PriceFragment fragment = new PriceFragment();
                fragment.setOutputView(mOutputView);
                fragment.setActionClickListener(mOnCalculatorActionClickListener);
                fragment.setActionLongClickListener(mOnCalculatorActionLongClickListener);
                result = fragment;
                break;
        }

        return result;
    }

    @Override
    public int getCount() {
        return 2;
    }
}
