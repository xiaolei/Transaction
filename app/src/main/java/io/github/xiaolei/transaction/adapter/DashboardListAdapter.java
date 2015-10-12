package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewholder.BaseViewHolder;
import io.github.xiaolei.transaction.viewmodel.DashboardListItem;

/**
 * TODO: add comment
 */
public class DashboardListAdapter extends GenericListAdapter<DashboardListItem, DashboardListAdapter.ViewHolder> {

    public DashboardListAdapter(Context context, List<DashboardListItem> items) {
        super(context, items);
    }

    @Override
    public void bindData(DashboardListItem viewModel) {
        mViewHolder.textViewLabel.setText(viewModel.label);
        mViewHolder.textViewValue.setText(viewModel.value);
    }

    @Override
    public DashboardListAdapter.ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.layout_item_dashboard;
    }

    public class ViewHolder extends BaseViewHolder {
        public TextView textViewLabel;
        public TextView textViewValue;

        public ViewHolder(View view) {
            textViewLabel = (TextView) view.findViewById(R.id.textViewLabel);
            textViewValue = (TextView) view.findViewById(R.id.textViewValue);
        }

    }
}
