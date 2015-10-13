package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import io.github.xiaolei.enterpriselibrary.utility.ColorUtil;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Tag;

/**
 * TODO: add comment
 */
public class TagListAdapter extends BaseAdapter implements IDataAdapter<Tag> {
    private List<Tag> mTags;
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;

    public TagListAdapter(Context context, List<Tag> tags) {
        mTags = tags;
        mContext = context;
        mInflater = LayoutInflater.from(context);
    }

    public void setTags(List<Tag> tags) {
        mTags = tags;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mTags.size();
    }

    @Override
    public Object getItem(int i) {
        return mTags.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mTags.get(i).getId();
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mInflater.inflate(R.layout.layout_item_tag, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            mViewHolder.imageViewTag.setBackgroundColor(ColorUtil.generateRandomColor());

            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        bindData(mTags.get(i));
        return view;
    }

    private void bindData(Tag tag) {
        if (mViewHolder == null) {
            return;
        }

        mViewHolder.textViewName.setText(tag.getName());
    }

    @Override
    public void append(List<Tag> data) {
        if (data != null) {
            mTags.addAll(data);
        }
    }

    @Override
    public void swapDate(List<Tag> data) {
        mTags.clear();
        mTags.addAll(data);
        notifyDataSetChanged();
    }

    private class ViewHolder {
        public TextView textViewName;
        public ImageView imageViewTag;

        public ViewHolder(View view) {
            textViewName = (TextView) view.findViewById(R.id.textViewTagName);
            imageViewTag = (ImageView) view.findViewById(R.id.imageViewTag);
        }
    }
}
