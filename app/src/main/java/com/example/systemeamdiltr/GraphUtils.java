package com.example.systemeamdiltr;

import android.content.Context;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GraphUtils {
    Context context;
    LineChart mChart;
    public long referenceTimestamp;
    public short dataIndex = 0;
    private ILineDataSet sets[] = new ILineDataSet[4];
    private int minLimit = 0, maxLimit = 0;

    private float maxValue;

    /**
     *
     * @param context
     * @param mChart
     */
    public GraphUtils(Context context, LineChart mChart) {

        this.context = context;
        this.mChart = mChart;
        this.mChart.animateXY(2000, 2000);
    }

    public GraphUtils(Context context, LineChart mChart,int minLimit, int maxLimit) {

        this(context, mChart);
        this.minLimit = minLimit;
        this.maxLimit = maxLimit;
    }

    /**
     *
     * @param referenceTimestamp
     * @param dateFormat datetime format to draw on the xaxis
     */
    public void initGraph(long referenceTimestamp, String dateFormat) {
        mChart.getDescription().setText(context.getResources().getString(R.string.chart_description));
        mChart.getDescription().setTextSize(18);
        mChart.getLegend().setTextSize(14);
        mChart.resetZoom();
        mChart.fitScreen();

        try {
            dataIndex = 0;
            mChart.clearValues();
            mChart.invalidate();


        } catch (Exception e) {
            e.printStackTrace();
        }

        mChart.setDrawGridBackground(false);
        LineData data2 = new LineData();
        data2.setValueTextColor(context.getResources().getColor(R.color.darkred));
        mChart.setData(data2);
        YAxis rightAxis2 = mChart.getAxisRight();
        rightAxis2.setDrawGridLines(true);
        rightAxis2.setEnabled(true);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextSize(12);
        xAxis.setLabelCount(6);
        xAxis.setGranularity(1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);
        this.referenceTimestamp = referenceTimestamp;
        xAxis.setValueFormatter(new DateXaxisValueFormatter(referenceTimestamp, dateFormat));


        xAxis.setTextColor(context.getResources().getColor(R.color.colorPrimary));
        xAxis.setDrawGridLines(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);
        mChart.setExtraTopOffset(10);
//        mChart.getLegend().getEntries()
    }

    private LineDataSet createSet(String DynamiqueData, int color, float width) {

        LineDataSet set = new LineDataSet(null, DynamiqueData);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(width);
        set.setColor(color);
//        set.setFillAlpha(110);
//        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setCircleColor(color);
        set.setHighlightEnabled(false);
        set.setCircleRadius(2f);
        set.setDrawValues(false);
        set.setDrawCircles(false);
//        set.setDrawValues(true);
        return set;
    }

    public void updateLegend() {
        Legend legend = mChart.getLegend();
        List<LegendEntry> legendEntry = new ArrayList<LegendEntry>(){};

        if (legend.getEntries().length>0) {
            legendEntry.add(legend.getEntries()[0]);
            legendEntry.add(legend.getEntries()[1]);
            legendEntry.add(legend.getEntries()[2]);
            legendEntry.add(legend.getEntries()[3]);
        }
        else return;
        legend.setEntries(legendEntry);
    }

    public void createSets(LineData data) {
        sets[0] = createSet("Min °C",context.getResources().getColor(android.R.color.holo_green_dark), 2f);
        sets[1] = createSet("Max °C",context.getResources().getColor(R.color.red), 2f);
        sets[2] = createSet("Normal °C",context.getResources().getColor(android.R.color.holo_orange_light), 2f);
        sets[3] = createSet("Température °C",context.getResources().getColor(android.R.color.holo_purple), 2f);
        sets[3].setValueFormatter(new TemperatureValueFormatter(2));
        sets[3].setDrawValues(true);
        for (ILineDataSet set: sets) data.addDataSet(set);
    }


    public void addEntry(long x, float y, float min, float max, float normal) {
        LineData data = mChart.getData();
        if (data != null) {
            sets[0] = data.getDataSetByIndex(dataIndex);
            sets[1] = data.getDataSetByIndex(dataIndex+1);
            sets[2] = data.getDataSetByIndex(dataIndex+2);
            sets[3] = data.getDataSetByIndex(dataIndex+3);

            if (sets[0] == null) {
                createSets(data);
            }

            maxValue = max;
            if (x==-1) x = sets[3].getEntryCount();
            data.addEntry(new Entry(x, min), dataIndex);
            data.addEntry(new Entry(x, max), dataIndex+1);
            data.addEntry(new Entry(x, normal), dataIndex+2);
            data.addEntry(new Entry(x, y), dataIndex+3);


            data.notifyDataChanged();

            // let the graph know it's data has changed
            mChart.notifyDataSetChanged();
            mChart.invalidate();
            updateLegend();
            if (minLimit!=0)
                mChart.setVisibleXRange(minLimit,maxLimit);
            mChart.moveViewToX(data.getEntryCount());

        }

    }


    public class DateXaxisValueFormatter extends IndexAxisValueFormatter {
        Map<String, String> mapFormats = new HashMap();

        DateFormat df;
        String valueToShow;
        private long referenceTimestamp;

        {
            mapFormats.put("today", "HH:mm:ss");
            mapFormats.put("week", "EEE HH:mm");
            mapFormats.put("month", "yy/MM/dd HH:mm");

        }

        public DateXaxisValueFormatter (long referenceTimestamp, String dateformat) {
            try {
            df = new SimpleDateFormat(mapFormats.get(dateformat));}
            catch (NullPointerException e)  {
                df = new SimpleDateFormat(mapFormats.get("month"));
            }
            this.referenceTimestamp = referenceTimestamp;
        }

        @Override
        public String getFormattedValue(float value) {
            long originalTimestamp = (long) value + referenceTimestamp;
            try {
                valueToShow = df.format(new Date(originalTimestamp));
            }catch (Exception e) {
                valueToShow = "16";
            }
            return valueToShow;
        }
    }

    public class TemperatureValueFormatter extends DefaultValueFormatter {
        Entry currentEntry;
        float max;
        /**
         *
         * @param digits
         */
        public TemperatureValueFormatter(int digits) {
            super(digits);
        }


        @Override
        public String getFormattedValue(float value) {
            ILineDataSet set;
            int i=1;
            do {
                set = mChart.getData().getDataSetByIndex(i);
                if (currentEntry.getX()<=
                        set.getEntryForIndex(set.getEntryCount()-1).getX())
                    break;
                i+=4;
            } while (i<dataIndex+2);
            max = set.getEntryForXValue(currentEntry.getX(), currentEntry.getY()).getY();
            if (value > max)
                return super.getFormattedValue(value);
            else
                return "";
        }

        @Override
        public String getPointLabel(Entry entry) {
            currentEntry = entry;
            return super.getPointLabel(entry);
        }
    }
}
