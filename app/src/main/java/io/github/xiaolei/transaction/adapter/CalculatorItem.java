package io.github.xiaolei.transaction.adapter;

import android.content.Context;

import io.github.xiaolei.transaction.ui.IFragmentSwitchable;
import io.github.xiaolei.transaction.widget.CalculatorOutputView;

/**
 * TODO: add comment
 */
public class CalculatorItem {
    public String text;
    public int actionId;
    public int textSize;
    public int backgroundResourceId;

    public CalculatorItem(String text, int actionId) {
        this.text = text;
        this.actionId = actionId;
    }

    public CalculatorItem(String text, int actionId, int textSize, int backgroundResourceId) {
        this(text, actionId);
        this.textSize = textSize;
        this.backgroundResourceId = backgroundResourceId;
    }
}
