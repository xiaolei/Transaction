package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.adapter.ProductNameAutoCompleteAdapter;
import io.github.xiaolei.transaction.entity.Product;
import io.github.xiaolei.transaction.repository.ProductRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;

/**
 * TODO: add comment
 */
public class ProductNameAutoCompleteTextView extends AutoCompleteTextView {
    private static final String TAG = ProductNameAutoCompleteTextView.class.getSimpleName();
    private ProductNameAutoCompleteAdapter mAdapter;

    public ProductNameAutoCompleteTextView(Context context) {
        super(context);
        initialize();
    }

    public ProductNameAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public ProductNameAutoCompleteTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initialize();
    }

    public void initialize() {
        if (isInEditMode()) {
            return;
        }

        setThreshold(1);
        mAdapter = new ProductNameAutoCompleteAdapter(getContext());
        setAdapter(mAdapter);
    }
}
