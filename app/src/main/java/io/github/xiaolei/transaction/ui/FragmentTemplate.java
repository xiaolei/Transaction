package io.github.xiaolei.transaction.ui;

import android.view.View;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class FragmentTemplate extends BaseFragment {
    private ViewHolder mViewHolder;

    public FragmentTemplate() {

    }

    @Override
    public int getActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    public void load() {

    }

    @Override
    public void initialize(View view) {
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
