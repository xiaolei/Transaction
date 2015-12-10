package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewmodel.ActionButtonInfo;

/**
 * TODO: add comment
 */
public class ActionButtonListAdapter extends GenericListAdapter<ActionButtonInfo, ActionButtonListAdapter.ViewHolder> {
    private static final String TAG = ActionButtonListAdapter.class.getSimpleName();

    public ActionButtonListAdapter(Context context, List<ActionButtonInfo> items) {
        super(context, items);
    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.item_action_button;
    }

    @Override
    public ViewHolder createViewHolder(View view) {
        return new ViewHolder(view);
    }

    @Override
    public void bindData(ViewHolder viewHolder, ActionButtonInfo viewModel) {
        viewHolder.imageViewAction.setImageResource(viewModel.iconResourceId);
        viewHolder.imageViewAction.setTag(viewModel);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).id;
    }

    public class ViewHolder {
        public ImageView imageViewAction;

        public ViewHolder(View view) {
            imageViewAction = (ImageView) view.findViewById(R.id.imageViewAction);
        }
    }
}
