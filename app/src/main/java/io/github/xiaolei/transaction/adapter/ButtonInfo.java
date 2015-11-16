package io.github.xiaolei.transaction.adapter;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public class ButtonInfo {
    public String text;
    public int actionId;
    public int textSize;
    public int backgroundResourceId;
    public int textColor;

    public ButtonInfo(String text, int actionId) {
        this.text = text;
        this.actionId = actionId;
    }

    public ButtonInfo(String text, int actionId, int textSize, int backgroundResourceId) {
        this(text, actionId, textSize, backgroundResourceId, R.drawable.button_text_selector);
    }

    public ButtonInfo(String text, int actionId, int textSize, int backgroundResourceId, int textColor) {
        this(text, actionId);
        this.textSize = textSize;
        this.backgroundResourceId = backgroundResourceId;
        this.textColor = textColor;
    }
}
