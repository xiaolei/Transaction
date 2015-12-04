package io.github.xiaolei.transaction.viewmodel;

import java.util.List;

/**
 * TODO: add comment
 */
public class LoadMoreReturnInfo<T> {
    public List<T> items;
    public boolean hasMore;

    public LoadMoreReturnInfo(List<T> items, boolean hasMore) {
        this.items = items;
        this.hasMore = hasMore;
    }
}
