package de.nisnagel.iogo.ui.history;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.support.AndroidSupportInjection;
import de.nisnagel.iogo.R;
import de.nisnagel.iogo.data.model.State;
import de.nisnagel.iogo.data.model.StateHistory;
import de.nisnagel.iogo.di.Injectable;
import de.nisnagel.iogo.service.Constants;
import de.nisnagel.iogo.service.DataBus;
import de.nisnagel.iogo.service.util.HistoryUtils;
import de.nisnagel.iogo.ui.detail.StateViewModel;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

    Toolbar toolbar;

    @BindView(R.id.txtValue)
    TextView mValue;
    @BindView(R.id.txtName)
    TextView mName;
    @BindView(R.id.btnDay)
    Button mDay;
    @BindView(R.id.btnWeek)
    Button mWeek;
    @BindView(R.id.btnMonth)
    Button mMonth;
    @BindView(R.id.btnYear)
    Button mYear;
    @BindView(R.id.chart)
    LineChart mChart;

    private StateViewModel mViewModel;
    private String stateId;
    private State state;
    private StateHistory stateHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBus.getBus().register(this);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(StateViewModel.class);
        stateId = getArguments().getString(Constants.ARG_STATE_ID);
        state = null;
        stateHistory = null;
        setHasOptionsMenu(true);
        toolbar = getActivity().findViewById(R.id.toolbar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);

        mName.setText(R.string.loading_date);

        mViewModel.getState(stateId).observe(this, new Observer<State>() {
            @Override
            public void onChanged(@Nullable State elem) {
                if (elem != null) {
                    mViewModel.setValue(elem.getVal());
                    mValue.setText(elem.getVal());
                    mName.setText(elem.getName());
                    state = elem;
                }
            }
        });

        mViewModel.getHistory(stateId).observe(this, new Observer<StateHistory>() {
            @Override
            public void onChanged(@Nullable StateHistory elem) {
                if (elem != null) {
                    if(stateHistory == null) {
                        stateHistory = elem;
                        showDay();
                    }else {
                        stateHistory = elem;
                    }
                }
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        DataBus.getBus().unregister(this);
    }

    private void setDataChart(String data) {
        List<Car> list;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        list = gson.fromJson(data, new TypeToken<List<Car>>() {
        }.getType());

        List<Entry> entries = new ArrayList<>();
        for (Car elem : list) {
            if(elem.ts != null && elem.val != null) {
                entries.add(new Entry(elem.ts / 1000f, elem.val));
            }
        }

        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setColor(Color.rgb(100, 255, 218));
        dataSet.setLineWidth(3f);
        dataSet.setDrawCircles(false);
        dataSet.setDrawValues(false);

        LineData lineData = new LineData(dataSet);
        mChart.clear();
        mChart.setData(lineData);
        mChart.setDrawBorders(false);
        mChart.getLegend().setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setTextColor(Color.WHITE);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setAxisMaximum(HistoryUtils.endOfDay);

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);
        yAxis.setDrawGridLines(false);

        mChart.getAxisRight().setEnabled(false);
        mChart.getDescription().setEnabled(false);
    }

    private void clearButtonStyles(){
        mDay.setTextColor(Color.BLACK);
        mWeek.setTextColor(Color.BLACK);
        mMonth.setTextColor(Color.BLACK);
        mYear.setTextColor(Color.BLACK);
    }

    @OnClick(R.id.btnDay)
    public void showDay() {

        clearButtonStyles();
        mDay.setTextColor(Color.WHITE);

        setDataChart(stateHistory.getDay());

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new DayXAxisValueFormatter());
        xAxis.setAxisMinimum(HistoryUtils.startOfDay);

        mChart.notifyDataSetChanged();
        mChart.invalidate();
    }

    @OnClick(R.id.btnWeek)
    public void showWeek() {

        clearButtonStyles();
        mWeek.setTextColor(Color.WHITE);

        setDataChart(stateHistory.getWeek());

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new WeekXAxisValueFormatter());
        xAxis.setAxisMinimum(HistoryUtils.startOfWeek);

        mChart.notifyDataSetChanged();
        mChart.invalidate();

    }

    @OnClick(R.id.btnMonth)
    public void showMonth() {

        clearButtonStyles();
        mMonth.setTextColor(Color.WHITE);

        setDataChart(stateHistory.getMonth());

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new MonthXAxisValueFormatter());
        xAxis.setAxisMinimum(HistoryUtils.startOfMonth);

        mChart.notifyDataSetChanged();
        mChart.invalidate();

    }

    @OnClick(R.id.btnYear)
    public void showYear() {

        clearButtonStyles();
        mYear.setTextColor(Color.WHITE);

        setDataChart(stateHistory.getYear());

        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(new YearXAxisValueFormatter());
        xAxis.setAxisMinimum(HistoryUtils.startOfYear);

        mChart.notifyDataSetChanged();
        mChart.invalidate();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    public class Car {
        public Long ts;
        public Float val;
    }

    public class DayXAxisValueFormatter implements IAxisValueFormatter {

        public DayXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return calendar.get(Calendar.HOUR_OF_DAY) + "h";
        }
    }

    public class WeekXAxisValueFormatter implements IAxisValueFormatter {

        public WeekXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + ".";
        }
    }

    public class MonthXAxisValueFormatter implements IAxisValueFormatter {

        public MonthXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH) + ".";
        }
    }

    public class YearXAxisValueFormatter implements IAxisValueFormatter {

        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        public YearXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return monthNames[calendar.get(Calendar.MONTH)];
        }
    }
}
