package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * 通用 ListAdapter. M: ViewModel 类型, V: ViewHolder 类型
 */
public abstract class GenericListAdapter<M, V> extends BaseAdapter {
    protected Context mContext;
    protected int mLayoutResourceId;
    protected List<M> mItems = new ArrayList<M>();
    protected V mViewHolder;

    public GenericListAdapter(Context context, List<M> items) {
        initialize(context);
        setItems(items);
    }

    public GenericListAdapter(Context context, M[] items) {
        initialize(context);
        setItems(items);
    }

    public Context getContext(){
        return mContext;
    }

    protected void initialize(Context context) {
        mContext = context;
        mLayoutResourceId = getLayoutResourceId();
    }

    protected void setItems(List<M> items) {
        mItems = new ArrayList<M>();
        if (items == null) {
            return;
        }

        mItems.addAll(items);
    }

    protected void setItems(M[] items) {
        if (items == null) {
            return;
        }

        List<M> list = new ArrayList<M>();
        for (M item : items) {
            list.add(item);
        }

        this.setItems(list);
    }

    public void swap(Collection<M> items) {
        mItems.clear();
        if (items != null) {
            mItems.addAll(items);
        }

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
            view = View.inflate(mContext, mLayoutResourceId, null);
            mViewHolder = createViewHolder(view);
            view.setTag(mViewHolder);
        } else {
            mViewHolder = (V) view.getTag();
        }

        if (mViewHolder != null) {
            bindData(mViewHolder, mItems.get(i));
        }

        return view;
    }

    public abstract int getLayoutResourceId();

    public abstract V createViewHolder(View view);

    public abstract void bindData(V viewHolder, M viewModel);
}
