package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.listener.EndlessRecyclerOnScrollListener;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.listener.OnOperationCompletedListener;
import io.github.xiaolei.transaction.viewholder.BaseViewHolder;
import io.github.xiaolei.transaction.viewholder.GenericRecyclerViewHolder;
import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;

/**
 * TODO: add comment
 */
public abstract class NewGenericRecyclerViewAdapter<T, V extends GenericRecyclerViewHolder> extends RecyclerView.Adapter<V> implements IDataAdapter<T> {
    protected static final int VIEW_TYPE_LOADING = 1;
    protected static final int VIEW_TYPE_DATA = 2;

    protected int mPageSize = 10;
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    protected List<T> mItems = new ArrayList<T>();
    protected V mViewHolder;
    protected LayoutInflater mLayoutInflater;
    protected OnRecyclerViewItemClickListener mOnItemClickListener;
    protected OnLoadMoreListener<T> mOnLoadMoreListener;
    protected EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    public NewGenericRecyclerViewAdapter(RecyclerView recyclerView, List<T> items, final OnLoadMoreListener<T> onLoadMoreListener) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        mOnLoadMoreListener = onLoadMoreListener;
        mItems = items;
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener<T>() {

            @Override
            public void onLoadMore(final int pageIndex, final OnOperationCompletedListener<LoadMoreReturnInfo<T>> listener) {
                AsyncTask<Void, Void, LoadMoreReturnInfo<T>> task = new AsyncTask<Void, Void, LoadMoreReturnInfo<T>>() {
                    @Override
                    protected LoadMoreReturnInfo<T> doInBackground(Void... params) {
                        return mOnLoadMoreListener.loadMore(pageIndex, pageIndex * mPageSize, mPageSize);
                    }

                    @Override
                    public void onPostExecute(LoadMoreReturnInfo<T> result) {
                        if(result != null){
                            append(result.items);
                        }
                        listener.onOperationCompleted(true, result, null);
                    }
                };
                task.execute();
            }
        };

        mRecyclerView.setOnScrollListener(mEndlessRecyclerOnScrollListener);
    }

    public int getPageSize() {
        return mPageSize;
    }

    @Override
    public void swap(List<T> items) {
        mItems.clear();

        if (items != null) {
            mItems.addAll(items);
            notifyDataSetChanged();
        }
    }

    @Override
    public void append(List<T> data) {
        if (data == null) {
            return;
        }

        mItems.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return (position >= mItems.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    protected abstract V createDataItemViewHolder(ViewGroup parent, int viewType);

    @Override
    public V onCreateViewHolder(ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_LOADING){
            return null;
        }

        V viewHolder = createDataItemViewHolder(parent, viewType);
        if (viewType == VIEW_TYPE_LOADING) {
            viewHolder.switchToLoadingView();
        } else {
            viewHolder.switchToDataView();
        }

        return viewHolder;
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    protected void onLoadMore(int pageIndex) {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.loadMore(pageIndex, mPageSize * pageIndex, mPageSize);
        }
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

    public T getItem(int position) {
        return mItems.get(position);
    }

    public interface OnRecyclerViewItemClickListener<T> {
        void onRecyclerViewItemClick(int position);
    }
}
