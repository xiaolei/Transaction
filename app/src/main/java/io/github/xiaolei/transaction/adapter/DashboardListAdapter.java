package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewholder.BaseViewHolder;
import io.github.xiaolei.transaction.viewmodel.DashboardListItem;

/**
 * TODO: add comment
 */
public class DashboardListAdapter extends GenericRecyclerViewAdapter<DashboardListItem, DashboardListAdapter.ViewHolder> {
    public DashboardListAdapter(Context context, List<DashboardListItem> items) {
        super(context, items);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.layout_item_dashboard, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, getOnItemClickListener());

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        DashboardListItem viewModel = getItems().get(position);
        holder.textViewLabel.setText(viewModel.label);
        holder.textViewValue.setText(viewModel.value);
    }

    public class ViewHolder extends BaseViewHolder {
        public TextView textViewLabel;
        public TextView textViewValue;


        public ViewHolder(View itemView, OnRecyclerViewItemClickListener listener) {
            super(itemView, listener);

            textViewLabel = (TextView) itemView.findViewById(R.id.textViewLabel);
            textViewValue = (TextView) itemView.findViewById(R.id.textViewValue);
        }
    }
}
