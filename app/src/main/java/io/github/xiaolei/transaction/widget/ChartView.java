package io.github.xiaolei.transaction.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;

import io.github.xiaolei.transaction.R;
import io.github.xiaolei.transaction.viewmodel.ChartDataSet;
import io.github.xiaolei.transaction.viewmodel.ChartValue;

/**
 * TODO: add comment
 */
public class ChartView extends RelativeLayout {
    protected static final String TAG = ChartView.class.getSimpleName();
    private ViewHolder mViewHolder;
    private ArrayList<ChartDataSet> mDataSets;

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initialize(context, attrs);
    }

    public ChartView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initialize(context, attrs);
    }

    public ChartView(Context context) {
        super(context);
        this.initialize(context, null);
    }

    public void bindData(ArrayList<ChartDataSet> chartDataSets) {
        if (chartDataSets == null || chartDataSets.size() == 0) {
            return;
        }

        mDataSets = chartDataSets;

        ArrayList<String> xValues = new ArrayList<String>();
        ArrayList<BarDataSet> lineDataSets = new ArrayList<BarDataSet>();

        for (ChartDataSet dataSet : chartDataSets) {
            ArrayList<BarEntry> yValues = new ArrayList<BarEntry>();
            BarDataSet barDataSet = new BarDataSet(yValues, dataSet.legend);
            barDataSet.setColor(dataSet.color);
            barDataSet.setValueTextColor(dataSet.color);
            lineDataSets.add(barDataSet);

            int index = 0;
            for (ChartValue value : dataSet.values) {
                if (!xValues.contains(value.xValue)) {
                    xValues.add(value.xValue);
                }

                BarEntry entry = new BarEntry(value.yValue.floatValue(), index);
                yValues.add(entry);

                index++;
            }
        }

        BarData data = new BarData(xValues, lineDataSets);
        mViewHolder.chart.setData(data);
        mViewHolder.chart.invalidate();
    }

    protected void initialize(Context context, AttributeSet attrs) {
        View view = View.inflate(context, R.layout.view_chart, this);
        int lineColor = getResources().getColor(R.color.light_gray);

        mViewHolder = new ViewHolder(view);
        mViewHolder.chart.setDescription("");
        mViewHolder.chart.setGridBackgroundColor(getResources().getColor(R.color.app_window_background));
        mViewHolder.chart.setScaleXEnabled(false);
        mViewHolder.chart.getXAxis().setGridColor(lineColor);
        mViewHolder.chart.getXAxis().setAxisLineColor(lineColor);
        mViewHolder.chart.getAxisRight().setAxisLineColor(lineColor);
        mViewHolder.chart.getAxisRight().setGridColor(lineColor);
        mViewHolder.chart.getAxisRight().setTextColor(lineColor);
        mViewHolder.chart.getAxisLeft().setAxisLineColor(lineColor);
        mViewHolder.chart.getAxisLeft().setGridColor(lineColor);
        mViewHolder.chart.getAxisLeft().setTextColor(lineColor);

        mViewHolder.chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry entry, int i, Highlight highlight) {
                Log.d(TAG, String.format("index: %d, value: %f", entry.getXIndex(), entry.getVal()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private class ViewHolder {
        public BarChart chart;

        public ViewHolder(View view) {
            chart = (BarChart) view.findViewById(R.id.chart);
        }
    }
}