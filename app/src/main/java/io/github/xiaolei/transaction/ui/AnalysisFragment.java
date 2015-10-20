package io.github.xiaolei.transaction.ui;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class AnalysisFragment extends BaseFragment {
    private ViewHolder mViewHolder;

    public AnalysisFragment() {
        setHasOptionsMenu(true);
    }

    public static AnalysisFragment newInstance() {
        AnalysisFragment fragment = new AnalysisFragment();
        return fragment;
    }

    @Override
    public int getActionBarTitle() {
        return R.string.analysis_fragment_title;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.empty_menu, menu);
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
        return R.layout.fragment_analysis;
    }

    private class ViewHolder {

        public ViewHolder(View view) {

        }
    }
}
