package io.github.xiaolei.transaction.ui;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import bolts.Task;
import de.greenrobot.event.EventBus;
import io.github.xiaolei.enterpriselibrary.utility.DateTimeUtils;
import io.github.xiaolei.enterpriselibrary.utility.DialogHelper;
import io.github.xiaolei.transaction.GlobalApplication;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.GenericEndlessAdapter;
import io.github.xiaolei.transaction.adapter.IPaginationDataLoader;
import io.github.xiaolei.transaction.adapter.TransactionListAdapter;
import io.github.xiaolei.transaction.entity.Transaction;
import io.github.xiaolei.transaction.event.FinishActionMode;
import io.github.xiaolei.transaction.event.RefreshTransactionListEvent;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.repository.TransactionRepository;
import io.github.xiaolei.transaction.util.ConfigurationManager;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * Transaction list fragment
 */
public class TransactionListFragment extends BaseFragment {
    public static final String TAG = TransactionListFragment.class.getSimpleName();
    public static final String ARG_TRANSACTION_START_DATE = "arg_transaction_start_date";
    public static final String ARG_TRANSACTION_END_DATE = "arg_transaction_end_date";

    private GenericEndlessAdapter<Transaction> mAdapter;
    private TransactionListAdapter mTransactionListAdapter;
    private ViewHolder mViewHolder;
    private Date mStartDate;
    private Date mEndDate;

    private MultiChoiceModeListener mActionModeCallback = new MultiChoiceModeListener();
    private android.view.ActionMode mActionMode;

    public TransactionListFragment() {

    }

    public static TransactionListFragment newInstance(Date startDate, Date endDate) {
        TransactionListFragment fragment = new TransactionListFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TRANSACTION_START_DATE, DateTimeUtils.getStartTimeOfDate(startDate).getTime());
        args.putLong(ARG_TRANSACTION_END_DATE, DateTimeUtils.getEndTimeOfDate(endDate).getTime());
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void initialize(View view) {
        mViewHolder = new ViewHolder(view);
        mViewHolder.listViewTransactions.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        mViewHolder.listViewTransactions.setMultiChoiceModeListener(new MultiChoiceModeListener());
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

    public void load(Date startDate, Date endDate) {
        mStartDate = DateTimeUtils.getStartTimeOfDate(startDate);
        mEndDate = DateTimeUtils.getEndTimeOfDate(endDate);

        load();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public Date getEndDate() {
        return mEndDate;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showDateRange(mStartDate, mEndDate);
    }

    private void showDateRange(Date startDate, Date endDate) {
        String dateRange = "";
        if (DateTimeUtils.isDateEqualsIgnoreTime(startDate, endDate)) {
            if (!DateUtils.isToday(startDate.getTime())) {
                dateRange = DateUtils.formatDateTime(getContext(), startDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY);
            } else {
                dateRange = getString(R.string.today);
            }
        } else {
            dateRange = String.format("%s ~ %s",
                    DateUtils.formatDateTime(getContext(), startDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY),
                    DateUtils.formatDateTime(getContext(), endDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_WEEKDAY));
        }

        mViewHolder.textViewTransactionDateRange.setText(dateRange);
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
    public int getActionBarTitle() {
        return R.string.transactions;
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
                if (!isAdded()) {
                    return;
                }

                if (result != null && result.size() > 0) {
                    mViewHolder.dataContainerViewTransactions.switchToDataView();

                    if (mTransactionListAdapter == null) {
                        mTransactionListAdapter = new TransactionListAdapter(getActivity(), result);

                        mAdapter = new GenericEndlessAdapter<Transaction>(getActivity(), mTransactionListAdapter, new IPaginationDataLoader<Transaction>() {
                            @Override
                            public List<Transaction> load(int offset, int limit) throws SQLException {
                                return query(mStartDate, mEndDate, offset, limit);
                            }
                        });

                        mViewHolder.listViewTransactions.setAdapter(mAdapter);
                    } else {
                        mTransactionListAdapter.swap(result);
                    }
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

    public void onEvent(FinishActionMode event) {
        finishActionModeIfNeeded();
    }

    @Override
    public void onPause() {
        super.onPause();
        finishActionModeIfNeeded();
    }

    public void finishActionModeIfNeeded() {
        if (mActionMode != null) {
            mActionMode.finish();
        }
    }

    private void removeTransactions(List<Transaction> transactions) {
        if (transactions == null || transactions.size() == 0) {
            return;
        }

        List<Long> transactionIds = new ArrayList<>();
        for (Transaction transaction : transactions) {
            transactionIds.add(transaction.getId());
        }

        AsyncTask<List<Long>, Void, String> task = new AsyncTask<List<Long>, Void, String>() {

            @Override
            protected String doInBackground(List<Long>... params) {
                List<Long> ids = params[0];
                String errorMessage = null;
                try {
                    RepositoryProvider.getInstance(getActivity()).resolve(TransactionRepository.class)
                            .removeTransactions(ids);
                } catch (SQLException e) {
                    e.printStackTrace();
                    errorMessage = e.getMessage();

                }

                return errorMessage;
            }

            @Override
            public void onPostExecute(String errorMessage) {
                load();
            }
        };
        task.execute(transactionIds);
    }

    private class MultiChoiceModeListener implements ListView.MultiChoiceModeListener {
        @Override
        public void onItemCheckedStateChanged(android.view.ActionMode actionMode, int position, long id, boolean checked) {
            int selectCount = mViewHolder.listViewTransactions.getCheckedItemCount();
            TransactionListAdapter adapter = mAdapter.getInnerAdapter(TransactionListAdapter.class);
            Transaction transaction = (Transaction) adapter.getItem(position);
            transaction.checked = checked;
            adapter.notifyDataSetChanged();

            switch (selectCount) {
                case 1:
                    actionMode.setSubtitle("One selected");
                    break;
                default:
                    actionMode.setSubtitle("" + selectCount + " selected");
                    break;
            }
        }

        @Override
        public boolean onCreateActionMode(android.view.ActionMode actionMode, Menu menu) {
            mActionMode = actionMode;
            actionMode.getMenuInflater().inflate(R.menu.action_mode_transaction_list, menu);
            actionMode.setTitle("Select Transactions");
            actionMode.setSubtitle("One Transaction selected");

            return true;
        }

        @Override
        public boolean onPrepareActionMode(android.view.ActionMode actionMode, Menu menu) {
            return true;
        }

        @Override
        public boolean onActionItemClicked(android.view.ActionMode actionMode, MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_delete_transaction:
                    DialogHelper.showConfirmDialog(getActivity(), getString(R.string.msg_confirm_remove_transactions), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeTransactions(mAdapter.getInnerAdapter(TransactionListAdapter.class).getCheckedItems());
                        }
                    });

                    break;
                default:
                    break;
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(android.view.ActionMode actionMode) {
            mActionMode = null;
            mTransactionListAdapter.uncheckAll();
        }
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
