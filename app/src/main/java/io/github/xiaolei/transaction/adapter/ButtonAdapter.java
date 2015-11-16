package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.widget.SquareRelativeLayout;

/**
 * TODO: add comment
 */
public class ButtonAdapter extends BaseAdapter implements IDataAdapter<ButtonInfo> {
    private List<ButtonInfo> mItems = new ArrayList<ButtonInfo>();
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;

    public ButtonAdapter(Context context, List<ButtonInfo> items) {
        mItems.addAll(items);
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setProducts(List<ButtonInfo> items) {
        mItems = items;
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public Object getItem(int i) {
        return mItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mItems.get(i).actionId;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_item_calculator, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        bindData(mItems.get(i));
        return view;
    }

    private void bindData(ButtonInfo item) {
        if (mViewHolder == null) {
            return;
        }

        mViewHolder.relativeLayoutButtonContainer.setBackgroundResource(item.backgroundResourceId);
        mViewHolder.textViewText.setText(item.text);
        mViewHolder.textViewText.setTextSize(TypedValue.COMPLEX_UNIT_SP, item.textSize);
        mViewHolder.textViewText.setTextColor(mContext.getResources().getColorStateList(item.textColor));
    }

    @Override
    public void append(List<ButtonInfo> data) {
        if (data != null) {
            mItems.addAll(data);
        }
    }

    @Override
    public void swap(List<ButtonInfo> data) {
        mItems.clear();
        mItems.addAll(data);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public TextView textViewText;
        public View view;
        public SquareRelativeLayout relativeLayoutButtonContainer;

        public ViewHolder(View view) {
            this.view = view;
            textViewText = (TextView) view.findViewById(R.id.textViewCalcItem);
            relativeLayoutButtonContainer = (SquareRelativeLayout) view.findViewById(R.id.relativeLayoutButtonContainer);
        }
    }
}
