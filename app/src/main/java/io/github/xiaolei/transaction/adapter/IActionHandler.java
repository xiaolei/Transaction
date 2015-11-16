package io.github.xiaolei.transaction.adapter;

import android.content.Context;

import io.github.xiaolei.transaction.widget.CalculatorOutputView;

/**
 * TODO: add comment
 */
public interface IActionHandler {
    void performAction(Context context, ButtonInfo actionItem, CalculatorOutputView outputView);
}
