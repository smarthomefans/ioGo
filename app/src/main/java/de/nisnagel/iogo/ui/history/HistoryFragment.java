/*
 * ioGo - android app to control ioBroker home automation server.
 *
 * Copyright (C) 2018  Nis Nagel
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.nisnagel.iogo.ui.history;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import de.nisnagel.iogo.service.util.HistoryUtils;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryFragment extends Fragment implements Injectable {

    @Inject
    ViewModelProvider.Factory mViewModelFactory;

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

    private HistoryViewModel mViewModel;
    private String stateId;
    private State state;
    private StateHistory stateHistory;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = ViewModelProviders.of(this, mViewModelFactory).get(HistoryViewModel.class);
        stateId = getArguments().getString(Constants.ARG_STATE_ID);
        state = null;
        stateHistory = null;
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history, container, false);
        ButterKnife.bind(this, rootView);

        mName.setText(R.string.main_loading_date);

        mViewModel.getState(stateId).observe(this, elem -> {
            if (elem != null) {
                mViewModel.setValue(elem.getVal());
                mValue.setText(elem.getVal());
                mName.setText(elem.getName());
                state = elem;
            }
        });

        mViewModel.getHistory(stateId).observe(this, elem -> {
            if (elem != null) {
                if (stateHistory == null) {
                    stateHistory = elem;
                    showDay();
                } else {
                    stateHistory = elem;
                }
            }
        });

        return rootView;
    }

    private void setDataChart(String data) {
        List<Car> list;
        GsonBuilder gsonBuilder = new GsonBuilder();
        Gson gson = gsonBuilder.create();
        list = gson.fromJson(data, new TypeToken<List<Car>>() {
        }.getType());

        List<Entry> entries = new ArrayList<>();
        if(list != null && list.size() > 0) {
            for (Car elem : list) {
                if (elem.ts != null && elem.val != null) {
                    entries.add(new Entry(elem.ts / 1000f, elem.val));
                }
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

    private void clearButtonStyles() {
        mDay.setTextColor(Color.BLACK);
        mWeek.setTextColor(Color.BLACK);
        mMonth.setTextColor(Color.BLACK);
        mYear.setTextColor(Color.BLACK);
    }

    @OnClick(R.id.btnDay)
    public void showDay() {
        if(stateHistory != null) {
            clearButtonStyles();
            mDay.setTextColor(Color.WHITE);

            setDataChart(stateHistory.getDay());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setValueFormatter(new DayXAxisValueFormatter());
            xAxis.setAxisMinimum(HistoryUtils.startOfDay);

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    @OnClick(R.id.btnWeek)
    public void showWeek() {
        if(stateHistory != null) {
            clearButtonStyles();
            mWeek.setTextColor(Color.WHITE);

            setDataChart(stateHistory.getWeek());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setValueFormatter(new WeekXAxisValueFormatter());
            xAxis.setAxisMinimum(HistoryUtils.startOfWeek);

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    @OnClick(R.id.btnMonth)
    public void showMonth() {
        if(stateHistory != null) {
            clearButtonStyles();
            mMonth.setTextColor(Color.WHITE);

            setDataChart(stateHistory.getMonth());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setValueFormatter(new MonthXAxisValueFormatter());
            xAxis.setAxisMinimum(HistoryUtils.startOfMonth);

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
    }

    @OnClick(R.id.btnYear)
    public void showYear() {
        if(stateHistory != null) {
            clearButtonStyles();
            mYear.setTextColor(Color.WHITE);

            setDataChart(stateHistory.getYear());

            XAxis xAxis = mChart.getXAxis();
            xAxis.setValueFormatter(new YearXAxisValueFormatter());
            xAxis.setAxisMinimum(HistoryUtils.startOfYear);

            mChart.notifyDataSetChanged();
            mChart.invalidate();
        }
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

    class Car {
        Long ts;
        Float val;
    }

    class DayXAxisValueFormatter implements IAxisValueFormatter {

        private DayXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return calendar.get(Calendar.HOUR_OF_DAY) + "h";
        }
    }

    class WeekXAxisValueFormatter implements IAxisValueFormatter {

        private WeekXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH)+1 + ".";
        }
    }

    class MonthXAxisValueFormatter implements IAxisValueFormatter {

        private MonthXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return calendar.get(Calendar.DAY_OF_MONTH) + "." + calendar.get(Calendar.MONTH)+1 + ".";
        }
    }

    class YearXAxisValueFormatter implements IAxisValueFormatter {

        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        private YearXAxisValueFormatter() {
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis((long) value * 1000);
            return monthNames[calendar.get(Calendar.MONTH)];
        }
    }
}
