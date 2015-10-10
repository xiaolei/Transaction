package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.Tag;
import io.github.xiaolei.transaction.ui.ProductEditorFragment;

/**
 * TODO: add comment
 */
public class ProductTagsAdapter extends BaseAdapter {
    private List<Tag> mTags;
    private Context mContext;
    private LayoutInflater mInflater;
    private ViewHolder mViewHolder;

    public ProductTagsAdapter(Context context, List<Tag> tags) {
        mTags = tags;
        mInflater = LayoutInflater.from(context);
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
            view = mInflater.inflate(R.layout.layout_item_product_tag, viewGroup, false);
            mViewHolder = new ViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) view.getTag();
        }

        bind(mTags.get(i));

        return view;
    }

    private void bind(Tag tag) {
        if (mViewHolder == null) {
            return;
        }

        mViewHolder.textViewProductTag.setText(tag.getName());
    }

    private class ViewHolder {
        public TextView textViewProductTag;

        public ViewHolder(View view) {
            textViewProductTag = (TextView) view.findViewById(R.id.textViewProductTag);
        }
    }
}
