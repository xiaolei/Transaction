package io.github.xiaolei.transaction.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toast;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.CurrencyHelper;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.DashboardListAdapter;
import io.github.xiaolei.transaction.adapter.GenericListAdapter;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.viewmodel.AmountInfo;
import io.github.xiaolei.transaction.viewmodel.ChartDataSet;
import io.github.xiaolei.transaction.viewmodel.ChartValue;
import io.github.xiaolei.transaction.viewmodel.DashboardListItem;
import io.github.xiaolei.transaction.widget.AmountView;
import io.github.xiaolei.transaction.widget.ChartView;

/**
 * TODO: add comment
 */
public class DashboardFragment extends BaseFragment implements GenericListAdapter.OnRecyclerViewItemClickListener {
    private ViewHolder mViewHolder;
    private DashboardListAdapter mAdapter;

    public DashboardFragment() {
    }

    public static DashboardFragment newInstance() {
        DashboardFragment fragment = new DashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.dashboard_fragment, menu);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.dashboard);
    }

    @Override
    public void load() {
        loadAmountInfoAsync();
        loadChartViewDataAsync();
        loadDashboardInfoAsync();
    }

    public void onEvent(RefreshTransactionListEvent event) {
        load();
    }

    private void loadAmountInfoAsync() {
        mViewHolder.amountViewInDashboard.setBusyIndicatorVisibility(true);
        AsyncTask<Void, Void, AmountInfo> task = new AsyncTask<Void, Void, AmountInfo>() {
            @Override
            protected AmountInfo doInBackground(Void... voids) {
                long accountId = GlobalApplication.getCurrentAccount().getId();
                String currencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
                TransactionRepository repository = RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class);
                AmountInfo result = new AmountInfo(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, currencyCode);

                try {
                    result.totalExpense = repository.getTotalOutgoing(accountId, currencyCode);
                    result.totalIncome = repository.getTotalIncoming(accountId, currencyCode);
                    result.amount = repository.getTotalAmount(accountId, currencyCode);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(AmountInfo result) {
                mViewHolder.amountViewInDashboard.setBusyIndicatorVisibility(false);
                mViewHolder.amountViewInDashboard.bindData(result);
            }
        };
        task.execute();
    }

    private void loadChartViewDataAsync() {
        AsyncTask<Void, Void, ArrayList<ChartDataSet>> task = new AsyncTask<Void, Void, ArrayList<ChartDataSet>>() {

            @Override
            protected ArrayList<ChartDataSet> doInBackground(Void... voids) {
                ArrayList<ChartDataSet> result = new ArrayList<ChartDataSet>();
                try {
                    long accountId = GlobalApplication.getCurrentAccount().getId();
                    String currencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
                    List<ChartValue> expense = RepositoryProvider.getInstance(getActivity())
                            .resolve(TransactionRepository.class)
                            .getExpenseTransactionsGroupByDay(accountId, currencyCode);
                    List<ChartValue> income = RepositoryProvider.getInstance(getActivity())
                            .resolve(TransactionRepository.class)
                            .getIncomeTransactionsGroupByDay(accountId, currencyCode);

                    result.add(new ChartDataSet(expense, getResources().getColor(R.color.expense_text_color), getString(R.string.outgoing)));
                    result.add(new ChartDataSet(income, getResources().getColor(R.color.incoming_text_color), getString(R.string.incoming)));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(ArrayList<ChartDataSet> result) {
                mViewHolder.chartView.bindData(result);
            }
        };
        task.execute();
    }

    private void loadDashboardInfoAsync() {
        AsyncTask<Void, Void, List<DashboardListItem>> task = new AsyncTask<Void, Void, List<DashboardListItem>>() {

            @Override
            protected List<DashboardListItem> doInBackground(Void... voids) {
                List<DashboardListItem> items = new ArrayList<DashboardListItem>();
                Transaction mostExpensiveTransaction = null;
                try {
                    String defaultCurrencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
                    TransactionRepository repository = RepositoryProvider.getInstance(getContext())
                            .resolve(TransactionRepository.class);
                    long accountId = GlobalApplication.getCurrentAccount().getId();

                    mostExpensiveTransaction = repository
                            .getMostExpensiveTransaction(accountId, defaultCurrencyCode);
                    if (mostExpensiveTransaction != null) {
                        items.add(new DashboardListItem("Most expensive transaction:\n" + mostExpensiveTransaction.getProduct().getName(),
                                CurrencyHelper.formatCurrency(defaultCurrencyCode, mostExpensiveTransaction.getProductPrice())));
                    }

                    long totalTransactionCount = repository.getTotalTransactionCount(accountId);
                    items.add(new DashboardListItem("Total transaction count: ", String.valueOf(totalTransactionCount)));

                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return items;
            }

            @Override
            protected void onPostExecute(List<DashboardListItem> result) {
                if (mViewHolder.listViewDashboard.getAdapter() == null) {
                    mAdapter = new DashboardListAdapter(getContext(), result);
                    mAdapter.setOnItemClickListener(DashboardFragment.this);
                    mViewHolder.listViewDashboard.setAdapter(mAdapter);
                } else {
                    ((DashboardListAdapter) (mViewHolder.listViewDashboard.getAdapter())).swap(result);
                }
            }
        };
        task.execute();
    }

    @Override
    public void findViews(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.listViewDashboard.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_dashboard;
    }

    @Override
    public void onRecyclerViewItemClick(int position) {
        Toast.makeText(getContext(), "Position:" + position, Toast.LENGTH_SHORT).show();
    }

    private class ViewHolder {
        public AmountView amountViewInDashboard;
        public ChartView chartView;
        public RecyclerView listViewDashboard;

        public ViewHolder(View view) {
            amountViewInDashboard = (AmountView) view.findViewById(R.id.assetSummaryViewInDashboard);
            chartView = (ChartView) view.findViewById(R.id.chartView);
            listViewDashboard = (RecyclerView) view.findViewById(R.id.listViewDashboard);
        }
    }
}
