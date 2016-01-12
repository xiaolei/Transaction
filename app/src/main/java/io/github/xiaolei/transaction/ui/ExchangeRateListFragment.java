package io.github.xiaolei.transaction.ui;

import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;
import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.ExchangeRateListAdapter;
import io.github.xiaolei.transaction.adapter.GenericRecyclerViewAdapter;
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
public class ExchangeRateListFragment extends BaseFragment implements OnLoadMoreListener<ExchangeRate>, GenericRecyclerViewAdapter.OnRecyclerViewItemClickListener {
    private ViewHolder mViewHolder;
    private ExchangeRateListAdapter mAdapter;
    private String mSearchKeywords = "";

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
                            .query(mSearchKeywords, 0, ConfigurationManager.DEFAULT_PAGE_SIZE);
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
            mAdapter.setOnItemClickListener(this);
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
                    .query(mSearchKeywords, offset, pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
            result = new ArrayList<ExchangeRate>();
        }

        return new LoadMoreReturnInfo<>(result, result.size() > 0);
    }

    @Override
    public void onRecyclerViewItemClick(final int position) {
        final ExchangeRate exchangeRate = mAdapter.getItem(position);
        DialogHelper.showInputDialog(getActivity(), getString(R.string.title_change_exchange_rate),
                CurrencyHelper.castToBigDecimal(exchangeRate.getExchangeRate()).toString(),
                InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL,
                new OnOperationCompletedListener<String>() {
                    @Override
                    public void onOperationCompleted(boolean success, String result, String message) {
                        updateExchangeRateAsync(position, exchangeRate.getId(), result);
                    }
                });
    }

    private void updateExchangeRateAsync(final int position, final long id, final String newValue) {
        if (TextUtils.isEmpty(newValue)) {
            return;
        }

        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    RepositoryProvider.getInstance(getActivity()).resolve(ExchangeRateRepository.class)
                            .updateExchangeRate(id, new BigDecimal(newValue));
                    return true;
                } catch (SQLException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (!success) {
                    Toast.makeText(getActivity(), getString(R.string.error_failed_to_update_exchange_rate), Toast.LENGTH_SHORT).show();
                    return;
                }

                load();
            }
        };
        task.execute();
    }

    private void initializeSearchView(final MenuItem searchMenuItem) {
        if (searchMenuItem == null) {
            return;
        }

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(getString(R.string.search_exchange_rate));

        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                mSearchKeywords = "";
                load();
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                mSearchKeywords = query;
                load();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                mSearchKeywords = newText;
                load();
                return true;
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.clear();
        inflater.inflate(R.menu.menu_exchange_rate_list, menu);

        initializeSearchView(menu.findItem(R.id.action_search_exchange_rate));
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
