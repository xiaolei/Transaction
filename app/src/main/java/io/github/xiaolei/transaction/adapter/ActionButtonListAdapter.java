package io.github.xiaolei.transaction.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.PhotoPicker;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewmodel.ActionButtonId;
import io.github.xiaolei.transaction.viewmodel.ActionButtonInfo;

/**
 * TODO: add comment
 */
public class ActionButtonListAdapter extends GenericListAdapter<ActionButtonInfo, ActionButtonListAdapter.ViewHolder> implements View.OnClickListener {
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
        viewHolder.imageViewAction.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        ActionButtonInfo actionButtonInfo = (ActionButtonInfo) v.getTag();
        switch (actionButtonInfo.id) {
            case ActionButtonId.PICK_PHOTO_FROM_GALLERY:
                PhotoPicker.getInstance().pickPhotoFromGallery((Activity) getContext());
                break;
            case ActionButtonId.TAKE_PHOTO:
                PhotoPicker.getInstance().takePhoto((Activity) getContext());
                break;
            default:
                break;
        }
    }

    public class ViewHolder {
        public ImageView imageViewAction;

        public ViewHolder(View view) {
            imageViewAction = (ImageView) view.findViewById(R.id.imageViewAction);
        }
    }
}
