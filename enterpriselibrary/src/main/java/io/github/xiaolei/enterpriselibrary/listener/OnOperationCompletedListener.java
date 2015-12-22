package io.github.xiaolei.enterpriselibrary.listener;

/**
 * TODO: add comment
 */
public interface OnOperationCompletedListener<T> {
    void onOperationCompleted(boolean success, T result, String message);
}
