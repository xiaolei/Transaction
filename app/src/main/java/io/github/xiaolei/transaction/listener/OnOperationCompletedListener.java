package io.github.xiaolei.transaction.listener;

/**
 * TODO: add comment
 */
public interface OnOperationCompletedListener<T> {
    void onOperationCompleted(boolean success, T result, String message);
}
