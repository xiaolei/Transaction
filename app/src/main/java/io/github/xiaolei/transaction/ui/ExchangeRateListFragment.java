package io.github.xiaolei.transaction.ui;

import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.ExchangeRateListAdapter;
import io.github.xiaolei.transaction.entity.ExchangeRate;
import io.github.xiaolei.transaction.listener.OnLoadMoreListener;
import io.github.xiaolei.transaction.repository.ExchangeRateRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.viewmodel.LoadMoreReturnInfo;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comment
 */
public class ExchangeRateListFragment extends BaseFragment implements OnLoadMoreListener<ExchangeRate> {
    private ViewHolder mViewHolder;
    private ExchangeRateListAdapter mAdapter;

    @Override
    public int getContentView() {
        return R.layout.fragment_exchange_rate_list;
    }

    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);
        RecyclerView.LayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        mViewHolder.recyclerViewExchangeRateList.setLayoutManager(layoutManager);
    }

    @Override
    public void load() {
        mViewHolder.dataContainerViewExchangeRateList.switchToBusyView();
        AsyncTask<Void, Void, List<ExchangeRate>> task = new AsyncTask<Void, Void, List<ExchangeRate>>() {

            @Override
            protected List<ExchangeRate> doInBackground(Void... params) {
                try {
                    return RepositoryProvider.getInstance(getActivity()).resolve(ExchangeRateRepository.class)
                            .query(0, ConfigurationManager.DEFAULT_PAGE_SIZE);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<ExchangeRate> result) {
                if (result != null && result.size() > 0) {
                    bindData(result);
                } else {
                    mViewHolder.dataContainerViewExchangeRateList.switchToEmptyView(getString(R.string.no_exchange_rate));
                }
            }
        };
        task.execute();
    }

    private void bindData(List<ExchangeRate> exchangeRateList) {
        if (mAdapter == null) {
            mAdapter = new ExchangeRateListAdapter(mViewHolder.recyclerViewExchangeRateList, exchangeRateList, this);
            mViewHolder.recyclerViewExchangeRateList.setAdapter(mAdapter);
        } else {
            mAdapter.swap(exchangeRateList);
        }

        mViewHolder.dataContainerViewExchangeRateList.switchToDataView();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.action_bar_title_exchange_rate_list;
    }

    @Override
    public LoadMoreReturnInfo<ExchangeRate> loadMore(int pageIndex, int offset, int pageSize) {
        List<ExchangeRate> result = null;
        try {
            result = RepositoryProvider.getInstance(getActivity()).resolve(ExchangeRateRepository.class)
                    .query(offset, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<ExchangeRate>();
        }

        return new LoadMoreReturnInfo<>(result, result.size() > 0);
    }

    private class ViewHolder {
        public DataContainerView dataContainerViewExchangeRateList;
        public RecyclerView recyclerViewExchangeRateList;

        public ViewHolder(View view) {
            dataContainerViewExchangeRateList = (DataContainerView) view.findViewById(R.id.dataContainerViewExchangeRateList);
            recyclerViewExchangeRateList = (RecyclerView) view.findViewById(R.id.recyclerViewExchangeRateList);
        }
    }
}
