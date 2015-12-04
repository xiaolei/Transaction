package io.github.xiaolei.transaction.listener;

import android.support.v7.widget.RecyclerView;

import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;

public abstract class EndlessRecyclerOnScrollListener<T> extends RecyclerView.OnScrollListener implements OnOperationCompletedListener<LoadMoreReturnInfo<T>> {
    private int currentPage = 0;
    private boolean loading = true;

    public int getCurrentPage() {
        return currentPage;
    }

    public boolean isLoading() {
        return loading;
    }

    public EndlessRecyclerOnScrollListener() {
    }

    public abstract void onLoadMore(int pageIndex, OnOperationCompletedListener<LoadMoreReturnInfo<T>> listener);

    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        if (loading) {
            return;
        }

        loading = true;
        onLoadMore(currentPage, this);
    }

    public void onLoadMoreCompleted(LoadMoreReturnInfo<T> result) {
        if (result != null && result.hasMore) {
            currentPage++;
        }
    }

    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        // Don't take any action on changed
    }

    @Override
    public void onOperationCompleted(boolean success, LoadMoreReturnInfo<T> result, String message) {
        onLoadMoreCompleted(result);
    }
}