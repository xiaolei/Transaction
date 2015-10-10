package io.github.xiaolei.transaction.adapter;

import java.util.List;

import io.github.xiaolei.transaction.entity.TableEntity;

/**
 * TODO: add comment
 */
public interface IDataAdapter<T> {
    void append(List<T> data);

    void swapDate(List<T> data);
}
