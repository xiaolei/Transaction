package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.viewholder.BaseViewHolder;
import io.github.xiaolei.transaction.viewmodel.BaseViewModel;

/**
 * TODO: add comment
 */
public abstract class GenericListAdapter<T extends BaseViewModel,
        V extends BaseViewHolder> extends BaseAdapter {
    protected Context mContext;
    protected int mLayoutResourceId;
    protected List<T> mItems = new ArrayList<T>();
    protected V mViewHolder;
    protected LayoutInflater mLayoutInflater;

    public GenericListAdapter(Context context, List<T> items) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mLayoutResourceId = getLayoutResourceId();
        mItems = items;
    }

    public void swap(List<T> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
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
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = mLayoutInflater.inflate(mLayoutResourceId, viewGroup, false);
            mViewHolder = createViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (V) view.getTag();
        }

        bindData(mItems.get(i));

        return view;
    }

    public abstract void bindData(T viewModel);

    public abstract V createViewHolder(View view);

    public abstract int getLayoutResourceId();
}
