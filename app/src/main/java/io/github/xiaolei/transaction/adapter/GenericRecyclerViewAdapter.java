package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewholder.BaseViewHolder;
import io.github.xiaolei.transaction.viewmodel.BaseViewModel;

/**
 * TODO: add comment
 */
public abstract class GenericRecyclerViewAdapter<T extends BaseViewModel, V extends BaseViewHolder> extends RecyclerView.Adapter<V> {
    protected Context mContext;
    protected List<T> mItems = new ArrayList<T>();
    protected V mViewHolder;
    protected LayoutInflater mLayoutInflater;
    protected OnRecyclerViewItemClickListener mOnItemClickListener;

    public GenericRecyclerViewAdapter(Context context, List<T> items) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(mContext);
        mItems = items;
    }

    public void swap(List<T> items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    protected void onItemClickListener(int position) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onRecyclerViewItemClick(position);
        }
    }

    protected OnRecyclerViewItemClickListener getOnItemClickListener() {
        return mOnItemClickListener;
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    protected LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    protected Context getContext() {
        return mContext;
    }

    protected List<T> getItems() {
        return mItems;
    }

    public interface OnRecyclerViewItemClickListener<T> {
        void onRecyclerViewItemClick(int position);
    }
}
