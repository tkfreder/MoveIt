package com.tinakit.moveit.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.tinakit.moveit.R;
import com.tinakit.moveit.db.FitnessDBHelper;
import com.tinakit.moveit.fragment.MyYAxisValueFormatter;
import com.tinakit.moveit.fragment.DemoBase;
import com.tinakit.moveit.model.User;
import com.tinakit.moveit.module.CustomApplication;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by Tina on 3/9/2016.
 */
public class Charts extends Fragment implements OnSeekBarChangeListener,
        OnChartValueSelectedListener {

    public static final String CHARTS_TAG= "CHARTS_TAG";
    public static final String CHARTS_BACKSTACK_NAME = "Charts";
    private static final int SEEKBAR_PERIOD_DAYS = 7;

    @Inject
    FitnessDBHelper mDatabaseHelper;

    protected BarChart mChart;
    private SeekBar mSeekBarX;
    private TextView tvX;
    private Typeface mTf;
    private List<User> userList;
    private ArrayList<String> xVals;
    private View mRootView;
    private FragmentActivity mFragmentActivity;
    private TextView mUnitPeriod;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.activity_barchart, container, false);
        mFragmentActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        return mRootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity){
            mFragmentActivity=(FragmentActivity) context;
            // DI
            ((CustomApplication)mFragmentActivity.getApplication()).getAppComponent().inject(this);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        tvX = (TextView)mRootView.findViewById(R.id.tvXMax);

        mSeekBarX = (SeekBar)mRootView.findViewById(R.id.seekBar1);

        mChart = (BarChart)mRootView.findViewById(R.id.chart1);
        mChart.setOnChartValueSelectedListener(this);

        mChart.setDrawBarShadow(false);
        mChart.setDrawValueAboveBar(true);


        mChart.setDescription("Weekly Elapsed Time");
        Display display = mFragmentActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mChart.setDescriptionPosition( size.x / 2, 64);

        // if more than 60 entries are displayed in the chart, no values will be
        // drawn
        //mChart.setMaxVisibleValueCount(60);

        // scaling can now only be done on x- and y-axis separately
        mChart.setPinchZoom(false);

        mChart.setDrawGridBackground(false);
        // mChart.setDrawYLabels(false);

        mTf = Typeface.createFromAsset(mFragmentActivity.getAssets(), "OpenSans-Regular.ttf");

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(mTf);
        xAxis.setDrawGridLines(false);
        xAxis.setSpaceBetweenLabels(2);

        YAxisValueFormatter custom = new MyYAxisValueFormatter();

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setDrawLabels(false);
        //leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(8, false);
        //leftAxis.setValueFormatter(custom);
        //leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        //leftAxis.setSpaceTop(15f);
        //leftAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)


        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setDrawLabels(false);
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(mTf);
        rightAxis.setLabelCount(8, false);
        //rightAxis.setValueFormatter(custom);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinValue(0f); // this replaces setStartAtZero(true)

        /*
        Legend l = mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_LEFT);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        //l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
         l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
         "def", "ghj", "ikl", "mno" });
        */

        userList = mDatabaseHelper.getUsers();
        xVals = new ArrayList<String>();
        for (User user : userList){
            xVals.add(user.getUserName());
        }
        setData(0, 0);

        // setting data
        mSeekBarX.setProgress(0);
        tvX.setText("");
        mSeekBarX.setOnSeekBarChangeListener(this);
        // mChart.setDrawLegend(false);
        mUnitPeriod = (TextView)mRootView.findViewById(R.id.unitPeriod);
        mUnitPeriod.setText(R.string.this_week);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.actionToggleValues: {
                for (IDataSet set : mChart.getData().getDataSets())
                    set.setDrawValues(!set.isDrawValuesEnabled());

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleHighlight: {
                if(mChart.getData() != null) {
                    mChart.getData().setHighlightEnabled(!mChart.getData().isHighlightEnabled());
                    mChart.invalidate();
                }
                break;
            }
            case R.id.actionTogglePinch: {
                if (mChart.isPinchZoomEnabled())
                    mChart.setPinchZoom(false);
                else
                    mChart.setPinchZoom(true);

                mChart.invalidate();
                break;
            }
            case R.id.actionToggleAutoScaleMinMax: {
                mChart.setAutoScaleMinMaxEnabled(!mChart.isAutoScaleMinMaxEnabled());
                mChart.notifyDataSetChanged();
                break;
            }
            case R.id.actionToggleHighlightArrow: {
                if (mChart.isDrawHighlightArrowEnabled())
                    mChart.setDrawHighlightArrow(false);
                else
                    mChart.setDrawHighlightArrow(true);
                mChart.invalidate();
                break;
            }
            case R.id.animateX: {
                mChart.animateX(3000);
                break;
            }
            case R.id.animateY: {
                mChart.animateY(3000);
                break;
            }
            case R.id.animateXY: {
                mChart.animateXY(3000, 3000);
                break;
            }
        }
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        setData(mSeekBarX.getProgress(), 0);
        if(mSeekBarX.getProgress() != 0){
            tvX.setText(String.valueOf((mSeekBarX.getProgress())));
            mUnitPeriod.setText(R.string.weeks_ago);
        }else{
            tvX.setText("");
            mUnitPeriod.setText(R.string.this_week);
        }
        mChart.invalidate();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub
    }

    private void setData(int count, float range) {

        ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();

        Calendar cal = new GregorianCalendar();
        cal.add(Calendar.DAY_OF_MONTH, -1 * count);
        Date daysAgo = cal.getTime();

        // get previous Sunday
        Date startDate = new Date();
        Calendar calendar =Calendar.getInstance();
        if (Calendar.DAY_OF_WEEK != calendar.SUNDAY){
            calendar.add( Calendar.DAY_OF_WEEK, -(calendar.get(Calendar.DAY_OF_WEEK)-1));
            calendar.add(Calendar.DAY_OF_MONTH, -SEEKBAR_PERIOD_DAYS * count);
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            startDate = calendar.getTime();
        }
        calendar.add(Calendar.DATE, SEEKBAR_PERIOD_DAYS);
        Date endDate = calendar.getTime();

        SparseArray<Float> timeList = mDatabaseHelper.getActivityTimes(startDate, endDate);
        int index = 0;
        for (User user : userList){
            DecimalFormat df = new DecimalFormat("#.##");
            if(timeList != null) {
                if (timeList.get(user.getUserId()) != null) {
                    yVals1.add(new BarEntry((float)timeList.get(user.getUserId()), index));
                }
            }
            index++;
        }

        BarDataSet set1 = new BarDataSet(yVals1, "Minutes of Activity");
        set1.setBarSpacePercent(35f);
        set1.setValueFormatter(new MinuteFormatter());

        ArrayList<IBarDataSet> dataSets = new ArrayList<IBarDataSet>();
        dataSets.add(set1);

        /*
        ////////////// second bar
        ArrayList<BarEntry> yVals2 = new ArrayList<BarEntry>();
        yVals2.add(new BarEntry(1f, 0));
        yVals2.add(new BarEntry(2f, 1));
        yVals2.add(new BarEntry(3f, 2));
        yVals2.add(new BarEntry(4f, 3));
        BarDataSet set2 = new BarDataSet(yVals2, "Total Distance");
        set2.setBarSpacePercent(35f);
        //set2.setValueFormatter(new MinuteFormatter());
        dataSets.add(set2);
*/
        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10f);
        data.setValueTypeface(mTf);

        mChart.setData(data);
    }

    public class MinuteFormatter implements ValueFormatter{
        @Override
        public String getFormattedValue(float totalSeconds, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {

            int hours = (int)((totalSeconds / 60)/60);
            int minutes = (int)(totalSeconds - (hours * 60 * 60))/60;
            int seconds = (int)totalSeconds - (minutes *60) - (hours * 60 * 60);

            String time = hours > 0 ? String.format("%02d:%02d:%02d", hours, minutes, seconds) :
                    String.format("%02d:%02d", minutes, seconds);
            return time;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {

        if (e == null)
            return;

        RectF bounds = mChart.getBarBounds((BarEntry) e);
        PointF position = mChart.getPosition(e, AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + mChart.getLowestVisibleXIndex() + ", high: "
                        + mChart.getHighestVisibleXIndex());
    }

    public void onNothingSelected() {
    };
}