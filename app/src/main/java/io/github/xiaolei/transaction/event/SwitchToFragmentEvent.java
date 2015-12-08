package io.github.xiaolei.transaction.event;

import android.os.Bundle;

/**
 * TODO: add comment
 */
public class SwitchToFragmentEvent {
    public String fragmentTagName;
    public Bundle arguments;

    public SwitchToFragmentEvent(String fragmentTagName, Bundle arguments) {
        this.fragmentTagName = fragmentTagName;
        this.arguments = arguments;
    }
}
