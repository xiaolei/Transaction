package io.github.xiaolei.enterpriselibrary.listener;

/**
 * TODO: add comment
 */
public interface OnCancellableOperationCompletedListener<T> {
    void onOperationCompleted(boolean cancelled, boolean success, T result, String message);
}
