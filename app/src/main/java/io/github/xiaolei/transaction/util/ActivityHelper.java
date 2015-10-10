package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.content.Intent;

import io.github.xiaolei.transaction.ui.ProductEditorActivity;

/**
 * TODO: add comment
 */
public class ActivityHelper {

    public static void startProductEditorActivity(Context context, long productId) {
        Intent intent = new Intent(context, ProductEditorActivity.class);
        intent.putExtra(ProductEditorActivity.ARG_PRODUCT_ID, productId);
        context.startActivity(intent);
    }
}
