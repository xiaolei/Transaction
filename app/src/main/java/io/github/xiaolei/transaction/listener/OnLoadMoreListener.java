package io.github.xiaolei.transaction.listener;

import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;

/**
 * TODO: add comment
 */
public interface OnLoadMoreListener<T> {
    LoadMoreReturnInfo<T> loadMore(int pageIndex, int offset, int pageSize);
}
