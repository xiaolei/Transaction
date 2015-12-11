package io.github.xiaolei.transaction.listener;

import android.support.v7.widget.RecyclerView;

import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;

public abstract class EndlessRecyclerOnScrollListener<T> extends RecyclerView.OnScrollListener implements OnOperationCompletedListener<LoadMoreReturnInfo<T>> {
    private int currentPage = 0;
    private boolean loading = false;
    private boolean mHasMore = true;

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isLoading() {
        return loading;
    }

    public void reset(){
        setCurrentPage(0);
        loading = false;
        mHasMore = true;
    }

    public EndlessRecyclerOnScrollListener() {
    }

    public abstract void onLoadMore(int pageIndex, OnOperationCompletedListener<LoadMoreReturnInfo<T>> listener);

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (loading || !mHasMore) {
            return;
        }

        loading = true;
        onLoadMore(currentPage, this);
    }

    public void onLoadMoreCompleted(LoadMoreReturnInfo<T> result) {
        loading = false;
        if (result != null && result.hasMore) {
            currentPage++;
        }

        mHasMore = result.hasMore;
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // Don't take any action on changed
    }

    @Override
    public void onOperationCompleted(boolean success, LoadMoreReturnInfo<T> result, String message) {
        onLoadMoreCompleted(result);
    }
}