package io.github.xiaolei.transaction.ui;

import android.view.View;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class UnlockFragment extends BaseFragment {
    private ViewHolder mViewHolder;

    public UnlockFragment() {

    }

    @Override
    public String getActionBarTitle() {
        return getResources().getString(R.string.unlock);
    }

    @Override
    public void load() {

    }

    @Override
    public void findViews(View view) {
        mViewHolder = new ViewHolder(view);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_unlock;
    }

    private class ViewHolder {

        public ViewHolder(View view) {

        }
    }
}
