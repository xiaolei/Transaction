package io.github.xiaolei.transaction.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.viewmodel.AmountInfo;
import io.github.xiaolei.transaction.viewmodel.ChartDataSet;
import io.github.xiaolei.transaction.viewmodel.ChartValue;
import io.github.xiaolei.transaction.widget.AmountView;
import io.github.xiaolei.transaction.widget.ChartView;

/**
 * TODO: add comment
 */
public class DashboardFragment extends BaseFragment {
    private ViewHolder mViewHolder;

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
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.dashboard_fragment, menu);
    }

    @Override
    public String getActionBarTitle() {
        return getString(R.string.dashboard);
    }

    @Override
    public void load() {
        loadAmountInfoAsync();
        loadChartViewDataAsync();
    }

    private void loadAmountInfoAsync() {
        mViewHolder.amountViewInDashboard.setBusyIndicatorVisibility(true);
        AsyncTask<Void, Void, AmountInfo> task = new AsyncTask<Void, Void, AmountInfo>() {
            @Override
            protected AmountInfo doInBackground(Void... voids) {
                String currencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
                TransactionRepository repository = RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class);
                AmountInfo result = new AmountInfo(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, currencyCode);

                try {
                    result.totalExpense = repository.getTotalOutgoing(currencyCode);
                    result.totalIncome = repository.getTotalIncoming(currencyCode);
                    result.amount = repository.getTotalAmount(currencyCode);
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
                    String currencyCode = GlobalApplication.getCurrentAccount().getDefaultCurrencyCode();
                    List<ChartValue> expense = RepositoryProvider.getInstance(getActivity())
                            .resolve(TransactionRepository.class)
                            .getExpenseTransactionsGroupByDay(currencyCode);
                    List<ChartValue> income = RepositoryProvider.getInstance(getActivity())
                            .resolve(TransactionRepository.class)
                            .getIncomeTransactionsGroupByDay(currencyCode);

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

    @Override
    public void findViews(View view) {
        mViewHolder = new ViewHolder(view);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_dashboard;
    }

    private class ViewHolder {
        public AmountView amountViewInDashboard;
        public ChartView chartView;

        public ViewHolder(View view) {
            amountViewInDashboard = (AmountView) view.findViewById(R.id.assetSummaryViewInDashboard);
            chartView = (ChartView) view.findViewById(R.id.chartView);
        }
    }
}
