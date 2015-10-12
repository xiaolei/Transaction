package io.github.xiaolei.transaction.adapter;

import android.content.Context;

import io.github.xiaolei.transaction.R;
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
    public int textColor;

    public CalculatorItem(String text, int actionId) {
        this.text = text;
        this.actionId = actionId;
    }

    public CalculatorItem(String text, int actionId, int textSize, int backgroundResourceId) {
        this(text, actionId, textSize, backgroundResourceId, R.drawable.button_text_selector);
    }

    public CalculatorItem(String text, int actionId, int textSize, int backgroundResourceId, int textColor) {
        this(text, actionId);
        this.textSize = textSize;
        this.backgroundResourceId = backgroundResourceId;
        this.textColor = textColor;
    }
}
