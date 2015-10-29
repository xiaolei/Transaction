package io.github.xiaolei.transaction.event;

/**
 * Controls the open state of the navigation drawer
 */
public class NavigationDrawerStateEvent {
    public boolean visibility;

    public NavigationDrawerStateEvent(boolean visible) {
        this.visibility = visible;
    }
}
