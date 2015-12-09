package io.github.xiaolei.transaction.listener;

/**
 * TODO: add comment
 */
public class PermissionResult {
    public String permission;
    public boolean granted;

    public PermissionResult(String permission, boolean granted) {
        this.permission = permission;
        this.granted = granted;
    }
}
