package io.github.xiaolei.transaction.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.ValidationError;
import com.mobsandgeeks.saripaar.Validator;

import java.util.List;

import io.github.xiaolei.transaction.R;

/**
 * TODO: add comment
 */
public abstract class BaseEditorFragment extends BaseFragment implements Validator.ValidationListener {
    private Validator mValidator;

    protected abstract void readArguments(Bundle args);

    protected abstract void load(long id) throws Exception;

    protected abstract void bind();

    protected abstract void preSave();

    protected abstract void save() throws Exception;

    protected abstract long getEntityId();

    protected abstract void onSaveCompleted(boolean success, Exception error);

    protected abstract Object getViewHolder();

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mValidator = new Validator(getViewHolder());
        mValidator.setValidationListener(this);

        Bundle args = getArguments();
        if (args != null) {
            readArguments(args);
        }

        loadAsync(getEntityId());
    }

    public void loadAsync(final long id) {
        AsyncTask<Void, Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    load(id);
                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                }

                return false;
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result == true) {
                    bind();
                } else {
                }
            }
        };
        task.execute();
    }

    public void validateThenSave() {
        mValidator.validate();
    }

    protected void saveAsync() {
        hideSoftKeyboard();
        final ProgressDialog dialog = ProgressDialog.show(getActivity(), "", getActivity().getString(R.string.saving));
        preSave();
        AsyncTask<Void, Void, Exception> task = new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... voids) {
                Exception result = null;
                try {
                    save();
                } catch (Exception e) {
                    result = e;
                    e.printStackTrace();
                }

                return result;
            }

            @Override
            protected void onPostExecute(Exception error) {
                dialog.dismiss();
                onSaveCompleted(error != null ? false : true, error);
            }
        };
        task.execute();
    }

    @Override
    public int getActionBarTitle() {
        return R.string.app_name;
    }

    @Override
    public void onValidationSucceeded() {
        saveAsync();
    }

    @Override
    public void onValidationFailed(List<ValidationError> errors) {
        for (ValidationError error : errors) {
            View view = error.getView();
            String message = error.getCollatedErrorMessage(getActivity());

            if (view instanceof EditText) {
                ((EditText) view).setError(message);
            } else {
                Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
            }
        }
    }
}
