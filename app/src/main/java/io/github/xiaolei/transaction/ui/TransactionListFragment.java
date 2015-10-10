package io.github.xiaolei.transaction.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.GenericEndlessAdapter;
import io.github.xiaolei.transaction.adapter.IPaginationDataLoader;
import io.github.xiaolei.transaction.adapter.TransactionListAdapter;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.util.DateTimeUtils;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * TODO: add comment
 */
public class TransactionListFragment extends BaseFragment {
    public static final String TAG = TransactionListFragment.class.getSimpleName();
    public static final String ARG_TRANSACTION_START_DATE = "arg_transaction_start_date";
    public static final String ARG_TRANSACTION_END_DATE = "arg_transaction_end_date";

    private GenericEndlessAdapter<Transaction> mAdapter;
    private ViewHolder mViewHolder;
    private Date mStartDate;
    private Date mEndDate;

    public TransactionListFragment() {
        setHasOptionsMenu(true);
    }

    public static TransactionListFragment newInstance(Date startDate, Date endDate) {
        TransactionListFragment fragment = new TransactionListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRANSACTION_START_DATE, DateTimeUtils.getStartTimeOfDate(startDate).getTime());
        args.putLong(ARG_TRANSACTION_END_DATE, DateTimeUtils.getEndTimeOfDate(endDate).getTime());
        fragment.setArguments(args);

        return fragment;
    }

    public void load(Date startDate, Date endDate) {
        mStartDate = DateTimeUtils.getStartTimeOfDate(startDate);
        mEndDate = DateTimeUtils.getEndTimeOfDate(endDate);

        load();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        showDateRange(mStartDate, mEndDate);
    }

    private void showDateRange(Date startDate, Date endDate) {
        mViewHolder.textViewTransactionDateRange.setText(String.format("%s ~ %s",
                DateTimeUtils.formatShortDate(startDate),
                DateTimeUtils.formatShortDate(endDate)));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        Bundle args = getArguments();
        if (args != null) {
            mStartDate = new Date(args.getLong(ARG_TRANSACTION_START_DATE, DateTimeUtils.getStartTimeOfDate(new Date()).getTime()));
            mEndDate = new Date(args.getLong(ARG_TRANSACTION_END_DATE, DateTimeUtils.getEndTimeOfDate(new Date()).getTime()));
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_transactions;
    }

    @Override
    public String getActionBarTitle() {
        return getResources().getString(R.string.transactions);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.transactions_fragment, menu);
    }

    private List<Transaction> query(Date startDate, Date endDate, long offset,
                                    long limit) {
        List<Transaction> result = null;

        try {
            result = RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class)
                    .query(GlobalApplication.getCurrentAccount().getId(),
                            startDate, endDate, offset, limit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public void load() {
        showDateRange(mStartDate, mEndDate);
        mViewHolder.dataContainerViewTransactions.switchToBusyView();
        AsyncTask<Void, Void, List<Transaction>> task = new AsyncTask<Void, Void, List<Transaction>>() {

            @Override
            protected List<Transaction> doInBackground(Void... voids) {
                List<Transaction> result = query(mStartDate, mEndDate, 0,
                        ConfigurationManager.DEFAULT_PAGE_SIZE);
                return result;
            }

            @Override
            protected void onPostExecute(List<Transaction> result) {
                if (result != null && result.size() > 0) {
                    mViewHolder.dataContainerViewTransactions.switchToDataView();

                    TransactionListAdapter adapter = new TransactionListAdapter(getActivity(), result);
                    mAdapter = new GenericEndlessAdapter<Transaction>(getActivity(), adapter, new IPaginationDataLoader<Transaction>() {
                        @Override
                        public List<Transaction> load(int offset, int limit) throws SQLException {
                            return query(mStartDate, mEndDate, offset, limit);
                        }
                    });
                    mViewHolder.listViewTransactions.setAdapter(mAdapter);

                } else {
                    mViewHolder.dataContainerViewTransactions.switchToEmptyView(getString(R.string.no_transaction));
                }
            }
        };
        task.execute();
    }

    public void onEvent(RefreshTransactionListEvent event) {
        load();
    }

    @Override
    public void findViews(View view) {
        mViewHolder = new ViewHolder(view);
    }

    private class ViewHolder {
        public ListView listViewTransactions;
        public DataContainerView dataContainerViewTransactions;
        public TextView textViewTransactionDateRange;

        public ViewHolder(View view) {
            listViewTransactions = (ListView) view.findViewById(R.id.listViewTransactions);
            dataContainerViewTransactions = (DataContainerView) view.findViewById(R.id.dataContainerViewTransactions);
            textViewTransactionDateRange = (TextView) view.findViewById(R.id.textViewTransactionDateRange);
        }
    }
}
