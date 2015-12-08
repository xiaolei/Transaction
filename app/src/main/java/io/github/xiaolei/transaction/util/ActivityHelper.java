package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.content.Intent;

import java.util.Date;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.event.SwitchToFragmentEvent;
import io.github.xiaolei.transaction.ui.NewTransactionActivity;
import io.github.xiaolei.transaction.ui.ProductEditorActivity;
import io.github.xiaolei.transaction.ui.TransactionEditorActivity;
import io.github.xiaolei.transaction.ui.TransactionNavigationFragment;

/**
 * TODO: add comment
 */
public class ActivityHelper {

    public static void startProductEditorActivity(Context context, long productId) {
        Intent intent = new Intent(context, ProductEditorActivity.class);
        intent.putExtra(ProductEditorActivity.ARG_PRODUCT_ID, productId);
        context.startActivity(intent);
    }

    public static void startNewTransactionActivity(Context context, Date transactionDate) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, NewTransactionActivity.class);
        if (transactionDate != null) {
            intent.putExtra(NewTransactionActivity.ARG_TRANSACTION_DATE, transactionDate.getTime());
        }

        context.startActivity(intent);
    }

    public static void startTransactionEditorActivity(Context context, long transactionId) {
        if (context == null) {
            return;
        }

        Intent intent = new Intent(context, TransactionEditorActivity.class);
        intent.putExtra(TransactionEditorActivity.ARG_TRANSACTION_ID, transactionId);
        context.startActivity(intent);
    }

    public static void goToTransactionList(Context context) {
        EventBus.getDefault().post(new SwitchToFragmentEvent(TransactionNavigationFragment.class.getName(), null));
    }
}
