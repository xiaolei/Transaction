package io.github.xiaolei.transaction.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ListAdapter;

import com.commonsware.cwac.endless.EndlessAdapter;

import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.entity.TableEntity;
import io.github.xiaolei.transaction.util.ConfigurationManager;

/**
 * TODO: add comment
 */
public class GenericEndlessAdapter<T extends TableEntity> extends EndlessAdapter {

    public static final String TAG = GenericEndlessAdapter.class.getSimpleName();
    private int mCurrentPage = 0;
    private List<T> data;
    private RotateAnimation rotate = null;
    private IPaginationDataLoader<T> mDataLoader;
    private ListAdapter mAdapter;

    public GenericEndlessAdapter(Context context, ListAdapter adapter, IPaginationDataLoader<T> dataLoader) {
        // Call super with the adapter that creates views for AdapterView.
        super(adapter);

        if (context == null) {
            throw new IllegalArgumentException("context cannot be null.");
        }

        if (!(adapter instanceof IDataAdapter)) {
            throw new IllegalArgumentException(adapter.getClass().getName() + " must implement IDataAdapter interface.");
        }

        if (dataLoader == null) {
            throw new IllegalArgumentException("dataLoader cannot be null.");
        }

        // Initialize filter parameters.
        this.mDataLoader = dataLoader;
        this.mAdapter = adapter;

        // Create a rotate animation.
        rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setDuration(600);
        rotate.setRepeatMode(Animation.RESTART);
        rotate.setRepeatCount(Animation.INFINITE);

    }

    public <T extends ListAdapter> T getInnerAdapter(Class<T> adapterType) {
        return adapterType.cast(mAdapter);
    }

    public void swapData(List<T> data) {
        ((IDataAdapter) mAdapter).swapDate(data);
        mCurrentPage = 0;
    }

    @Override
    protected View getPendingView(ViewGroup parent) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_load_more, null);
        return row;
    }

    @Override
    protected boolean cacheInBackground() throws Exception {
        // Called on the background thread when the adapter needs to fetch new data.
        // Load the results and return true if there are more records remaining after this load.

        // Increase page counter.
        ++mCurrentPage;

        // Load next page of users.
        int offset = mCurrentPage * ConfigurationManager.DEFAULT_PAGE_SIZE;
        Log.d(TAG, String.format("load(offset=%d, limit=%d)", offset, ConfigurationManager.DEFAULT_PAGE_SIZE));
        data = mDataLoader.load(offset, ConfigurationManager.DEFAULT_PAGE_SIZE);

        if (data == null || data.size() == 0) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    protected void appendCachedData() {
        // Append the cached data to wrapped adapter.
        IDataAdapter wrappedAdapter = (IDataAdapter) mAdapter;
        wrappedAdapter.append(data);
    }


    private void startProgressAnimation(View pendingView) {
        if (pendingView != null) {
            pendingView.startAnimation(rotate);
        }
    }
}
