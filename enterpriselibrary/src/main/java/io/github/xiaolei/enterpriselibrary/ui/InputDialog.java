package io.github.xiaolei.enterpriselibrary.ui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import io.github.xiaolei.enterpriselibrary.R;
import io.github.xiaolei.enterpriselibrary.listener.OnOperationCompletedListener;

/**
 * Input dialog
 */
public class InputDialog extends DialogFragment {
    public static final String TAG = InputDialog.class.getSimpleName();
    public static final String ARG_DEFAULT_TEXT = "arg_default_text";
    public static final String ARG_TITLE = "arg_title";

    private Context mContext;
    private ViewHolder mViewHolder;
    private String mDefaultText = "";
    private String mTitle;
    private OnOperationCompletedListener<String> mOnOperationCompletedListener;

    public static void showDialog(Context context, FragmentManager fragmentManager, String title, String defaultText,
                                  OnOperationCompletedListener<String> onOperationCompletedListener) {
        InputDialog fragment = new InputDialog();
        fragment.setOnUserCompletedInputListener(onOperationCompletedListener);
        fragment.mContext = context;

        Bundle args = new Bundle();
        if (!TextUtils.isEmpty(defaultText)) {
            args.putString(ARG_DEFAULT_TEXT, defaultText);
        }

        if (!TextUtils.isEmpty(title)) {
            args.putString(ARG_TITLE, title);
        }

        fragment.setArguments(args);
        fragment.show(fragmentManager, TAG);
    }

    public void setOnUserCompletedInputListener(OnOperationCompletedListener<String> onOperationCompletedListener) {
        mOnOperationCompletedListener = onOperationCompletedListener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args != null) {
            mDefaultText = args.getString(ARG_DEFAULT_TEXT);
            mTitle = args.getString(ARG_TITLE);
        }

        View view = View.inflate(getActivity(), R.layout.dialog_fragment_input, null);
        mViewHolder = new ViewHolder(view);
        mViewHolder.editTextProductName.setText(mDefaultText);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setTitle(mTitle)
                .setView(view)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hideSoftKeyboard();
                        if (mOnOperationCompletedListener != null) {
                            mOnOperationCompletedListener.onOperationCompleted(true, mViewHolder.editTextProductName.getText().toString(), null);
                        }
                    }
                })
                .setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                hideSoftKeyboard();
                                dialog.dismiss();
                            }
                        }
                )
                .create();

        return dialog;
    }

    protected void hideSoftKeyboard() {
        if(!isAdded()){
            return;
        }

        View view = getDialog().getWindow().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        mViewHolder.editTextProductName.selectAll();
    }

    private class ViewHolder {
        public EditText editTextProductName;

        public ViewHolder(View view) {
            editTextProductName = (EditText) view.findViewById(R.id.editTextProductName);
        }
    }
}
