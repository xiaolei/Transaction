package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.logging.Logger;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.listener.EndlessRecyclerOnScrollListener;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.listener.OnOperationCompletedListener;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.viewholder.GenericRecyclerViewHolder;
import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;

/**
 * TODO: add comment
 */
public abstract class EndlessGenericRecyclerViewAdapter<T, V extends GenericRecyclerViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements IDataAdapter<T> {
    protected static final int VIEW_TYPE_LOADING = 1;
    protected static final int VIEW_TYPE_DATA = 2;
    private static final String TAG = EndlessGenericRecyclerViewAdapter.class.getSimpleName();

    protected int mPageSize = ConfigurationManager.DEFAULT_PAGE_SIZE;
    protected Context mContext;
    protected RecyclerView mRecyclerView;
    protected List<T> mItems = new ArrayList<T>();
    protected V mViewHolder;
    protected LayoutInflater mLayoutInflater;
    protected OnLoadMoreListener<T> mOnLoadMoreListener;
    protected EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;
    private int mOffset = 0;

    public EndlessGenericRecyclerViewAdapter(RecyclerView recyclerView, List<T> items, final OnLoadMoreListener<T> onLoadMoreListener) {
        mRecyclerView = recyclerView;
        mContext = recyclerView.getContext();
        mLayoutInflater = LayoutInflater.from(mContext);
        mOnLoadMoreListener = onLoadMoreListener;
        mItems = items;
        mOffset = mItems.size();
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener<T>() {

            @Override
            public void onLoadMore(final int pageIndex, final OnOperationCompletedListener<LoadMoreReturnInfo<T>> listener) {
                // Show loading footer
                appendItem(null);
                AsyncTask<Void, Void, LoadMoreReturnInfo<T>> task = new AsyncTask<Void, Void, LoadMoreReturnInfo<T>>() {
                    @Override
                    protected LoadMoreReturnInfo<T> doInBackground(Void... params) {
                        Logger.d(TAG, String.format("loadMore => pageIndex: %d, offset: %d, pageSize: %d", pageIndex, mOffset + pageIndex * mPageSize, mPageSize));
                        return mOnLoadMoreListener.loadMore(pageIndex, mOffset + pageIndex * mPageSize, mPageSize);
                    }

                    @Override
                    public void onPostExecute(LoadMoreReturnInfo<T> result) {
                        if (result != null) {
                            append(result.items);
                        }

                        // Hide loading footer
                        removeItem(null);
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

    protected void removeItem(Object item) {
        mItems.remove(item);
        notifyDataSetChanged();
    }

    protected void appendItem(Object item) {
        mItems.add(null);
        notifyDataSetChanged();
    }

    @Override
    public void swap(List<T> items) {
        mItems.clear();

        if (items != null) {
            mItems.addAll(items);
        }

        mEndlessRecyclerOnScrollListener.reset();
        mOffset = items != null ? items.size() : 0;

        notifyDataSetChanged();
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
        return mItems.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_DATA;
    }

    protected abstract V createDataItemViewHolder(ViewGroup parent, int viewType);

    protected abstract void bindData(V viewHolder, T item);

    protected void bindLoadingView(RecyclerView.ViewHolder viewHolder) {
    }

    protected RecyclerView.ViewHolder createLoadingViewHolder(ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.layout_footer_loading, parent, false);
        return new FooterLoadingViewHolder(view);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == VIEW_TYPE_DATA) {
            viewHolder = createDataItemViewHolder(parent, viewType);
        } else {
            viewHolder = createLoadingViewHolder(parent, viewType);
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) == VIEW_TYPE_DATA) {
            bindData((V) holder, mItems.get(position));
        } else {
            bindLoadingView(holder);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    protected void onLoadMore(int pageIndex) {
        if (mOnLoadMoreListener != null) {
            mOnLoadMoreListener.loadMore(pageIndex, mPageSize * pageIndex, mPageSize);
        }
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

    public class FooterLoadingViewHolder extends RecyclerView.ViewHolder {

        public FooterLoadingViewHolder(View itemView) {
            super(itemView);
        }
    }
}
