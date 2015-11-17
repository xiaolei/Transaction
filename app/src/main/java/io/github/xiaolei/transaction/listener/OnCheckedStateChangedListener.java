package io.github.xiaolei.transaction.listener;

/**
 * TODO: add comment
 */
public interface OnCheckedStateChangedListener<T> {
    void onCheckedStateChanged(T sender, boolean checked);
}
