package io.github.xiaolei.transaction.util;

import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.entity.Photo;
import io.github.xiaolei.transaction.event.SwitchToFragmentEvent;
import io.github.xiaolei.transaction.ui.NewTransactionActivity;
import io.github.xiaolei.transaction.ui.PhotoListActivity;
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

    public static void startPhotoListActivity(Context context, ArrayList<Photo> photoList, int currentPosition) {
        if (context == null || photoList == null || photoList.size() == 0) {
            return;
        }

        Intent intent = new Intent(context, PhotoListActivity.class);
        intent.putExtra(PhotoListActivity.ARG_PHOTO_URLS, photoList);
        intent.putExtra(PhotoListActivity.ARG_CURRENT_POSITION, currentPosition);
        context.startActivity(intent);
    }
}
