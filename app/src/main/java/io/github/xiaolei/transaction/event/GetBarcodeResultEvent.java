package io.github.xiaolei.transaction.event;

import com.journeyapps.barcodescanner.BarcodeResult;

/**
 * TODO: add comment
 */
public class GetBarcodeResultEvent {
    public BarcodeResult barcodeResult;

    public GetBarcodeResultEvent(BarcodeResult barcodeResult) {
        this.barcodeResult = barcodeResult;
    }
}
