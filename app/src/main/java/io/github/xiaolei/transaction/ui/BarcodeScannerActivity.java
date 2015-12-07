package io.github.xiaolei.transaction.ui;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.zxing.ResultPoint;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.event.GetBarcodeResultEvent;

public class BarcodeScannerActivity extends BaseActivity implements BarcodeCallback {
    public static final String TAG = BarcodeScannerActivity.class.getSimpleName();
    private ViewHolder mViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_scanner);
        setupToolbar(R.id.toolbar, false);

        initialize();
    }

    public void initialize() {
        mViewHolder = new ViewHolder(this);
        mViewHolder.barcodeView.decodeContinuous(this);
    }

    @Override
    public void barcodeResult(BarcodeResult barcodeResult) {
        if (!TextUtils.isEmpty(barcodeResult.getText())) {
            mViewHolder.barcodeView.setStatusText(barcodeResult.getText());
            EventBus.getDefault().post(new GetBarcodeResultEvent(barcodeResult));
            finish();
        }
    }

    @Override
    public void possibleResultPoints(List<ResultPoint> list) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        mViewHolder.barcodeView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();

        mViewHolder.barcodeView.pause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return mViewHolder.barcodeView.onKeyDown(keyCode, event) || super.onKeyDown(keyCode, event);
    }

    private class ViewHolder {
        public CompoundBarcodeView barcodeView;

        public ViewHolder(Activity activity) {
            barcodeView = (CompoundBarcodeView) activity.findViewById(R.id.zxing_barcode_scanner);
        }
    }
}
