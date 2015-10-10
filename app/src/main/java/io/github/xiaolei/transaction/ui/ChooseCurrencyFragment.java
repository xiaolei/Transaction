package io.github.xiaolei.transaction.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.adapter.CurrencyCheckListAdapter;
import io.github.xiaolei.transaction.entity.ExchangeRate;
import io.github.xiaolei.transaction.listener.OnFragmentDialogDismissListener;
import io.github.xiaolei.transaction.repository.ExchangeRateRepository;
import io.github.xiaolei.transaction.repository.RepositoryProvider;
import io.github.xiaolei.transaction.util.PreferenceHelper;
import io.github.xiaolei.transaction.viewmodel.CurrencyCheckListItem;
import io.github.xiaolei.transaction.widget.DataContainerView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChooseCurrencyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChooseCurrencyFragment extends DialogFragment {
    public static final String TAG = ChooseCurrencyFragment.class.getSimpleName();
    private static final String ARG_CHECKED_CURRENCY_CODE = "checked_currency_code";
    private String mCheckedCurrencyCode;
    private ViewHolder mViewHolder;
    private CurrencyCheckListAdapter mAdapter;
    private OnFragmentDialogDismissListener<String> mOnFragmentDialogDismissListener;

    public static ChooseCurrencyFragment newInstance(String checkedCurrencyCode) {
        ChooseCurrencyFragment fragment = new ChooseCurrencyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CHECKED_CURRENCY_CODE, checkedCurrencyCode);
        fragment.setArguments(args);

        return fragment;
    }

    public ChooseCurrencyFragment() {
        // Required empty public constructor
    }

    public void setOnFragmentDialogDismissListener(OnFragmentDialogDismissListener listener) {
        mOnFragmentDialogDismissListener = listener;
    }

    protected void onFragmentDialogDismissListener(String checkedCurrencyCode) {
        if (mOnFragmentDialogDismissListener != null) {
            mOnFragmentDialogDismissListener.onFragmentDialogDismiss(checkedCurrencyCode);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCheckedCurrencyCode = getArguments().getString(ARG_CHECKED_CURRENCY_CODE);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_choose_currency, null);
        mViewHolder = new ViewHolder(view);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(getString(R.string.dialog_title_choose_currency));
        alertDialogBuilder.setView(view);
        alertDialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
                mCheckedCurrencyCode = mAdapter.getCheckedCurrencyCode();
                onFragmentDialogDismissListener(mCheckedCurrencyCode);
            }
        });
        alertDialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mCheckedCurrencyCode = mAdapter.getCheckedCurrencyCode();
                onFragmentDialogDismissListener(mCheckedCurrencyCode);
            }
        });

        initialize();
        load();

        return alertDialogBuilder.create();
    }

    private void initialize() {
        mViewHolder.listViewCurrencyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.findViewById(R.id.radioButtonChooseCurrency).performClick();
            }
        });
    }

    private void load() {
        mViewHolder.dataContainerChooseCurrency.switchToBusyView();
        final int[] checkedItemPosition = {0};
        AsyncTask<Void, Void, List<CurrencyCheckListItem>> task = new AsyncTask<Void, Void, List<CurrencyCheckListItem>>() {
            @Override
            protected List<CurrencyCheckListItem> doInBackground(Void... voids) {
                List<CurrencyCheckListItem> result = new ArrayList<CurrencyCheckListItem>();
                try {
                    Dao<ExchangeRate, Long> dao = RepositoryProvider.getInstance(getActivity()).resolve(ExchangeRateRepository.class).getDataAccessObject(ExchangeRate.class);
                    QueryBuilder<ExchangeRate, Long> query = dao.queryBuilder();
                    query.orderBy(ExchangeRate.CURRENCY_CODE, true);
                    List<ExchangeRate> queryResult = dao.query(query.prepare());

                    if (queryResult != null) {
                        int position = 0;
                        for (ExchangeRate rate : queryResult) {
                            Currency currency = Currency.getInstance(rate.getCurrencyCode());
                            String displayName = String.format("%s", rate.getCurrencyCode());
                            CurrencyCheckListItem item = new CurrencyCheckListItem(rate.getCurrencyCode(), displayName);
                            if (TextUtils.equals(rate.getCurrencyCode(), mCheckedCurrencyCode)) {
                                checkedItemPosition[0] = position;
                            }

                            result.add(item);
                            position++;
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(List<CurrencyCheckListItem> result) {
                mViewHolder.dataContainerChooseCurrency.switchToDataView();
                mAdapter = new CurrencyCheckListAdapter(getActivity(), result, mCheckedCurrencyCode);
                mViewHolder.listViewCurrencyList.setAdapter(mAdapter);
                if (checkedItemPosition[0] != 0) {
                    mViewHolder.listViewCurrencyList.setSelection(checkedItemPosition[0]);
                }
            }
        };
        task.execute();
    }

    public String getCheckedCurrencyCode() {
        return mAdapter != null ? mAdapter.getCheckedCurrencyCode() : PreferenceHelper.DEFAULT_CURRENCY_CODE;
    }

    private class ViewHolder {
        public ListView listViewCurrencyList;
        public DataContainerView dataContainerChooseCurrency;

        public ViewHolder(View view) {
            listViewCurrencyList = (ListView) view.findViewById(R.id.listViewCurrencyList);
            dataContainerChooseCurrency = (DataContainerView) view.findViewById(R.id.dataContainerChooseCurrency);
        }
    }
}
