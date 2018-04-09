package com.example.nagel.io1.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.model.Temperature;
import com.example.nagel.io1.view.adapter.TemperatureListAdapter;
import com.example.nagel.io1.view.ui.TemperatureActivity;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ListViewModel extends ViewModel {


    private String [] aktienlisteArray = {
            "Adidas - Kurs: 73,45 €",
            "Allianz - Kurs: 145,12 €",
            "BASF - Kurs: 84,27 €",
            "Bayer - Kurs: 128,60 €",
            "Beiersdorf - Kurs: 80,55 €"
    };
    private List <String> aktienListe = new ArrayList<>(Arrays.asList(aktienlisteArray));

    private MutableLiveData<List<String>> mListMutableLiveData;
    private MutableLiveData<List<Temperature>> mTempListMutableLiveData;

    // Create a LiveData with a String
    private MutableLiveData<String> mCurrentName;

    public MutableLiveData<List<String>> getList() {
        if (mListMutableLiveData == null) {
            mListMutableLiveData = new MutableLiveData<>();
            loadList();
        }
        return mListMutableLiveData;
    }

    private void loadList() {
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long seed = System.nanoTime();
                Collections.shuffle(aktienListe, new Random(seed));

                mListMutableLiveData.setValue(aktienListe);
            }
        }, 5000);

    }

    public MutableLiveData<List<Temperature>> getTempList() {
        if (mTempListMutableLiveData == null) {
            mTempListMutableLiveData = new MutableLiveData<>();
            loadTempList();
            DataBus.getBus().register(this);
        }
        return mTempListMutableLiveData;
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        if("javascript.0.vi_text".equals(event.getId())){
            List<Temperature> list = new ArrayList<>();
            Temperature t = new Temperature("Dummy1", "21.9°");
            list.add(t);
            t = new Temperature("Dummy2", "22.9°");
            list.add(t);
            t = new Temperature("Dummy3", "23.9°");
            list.add(t);
            t = new Temperature("Dummy4", event.getText()+"°C");
            list.add(t);

            mTempListMutableLiveData.postValue(list);
        }
    }


    private void loadTempList() {
        Handler myHandler = new Handler();
        myHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long seed = System.nanoTime();
                List<Temperature> list = new ArrayList<>();
                Temperature t = new Temperature("Dummy1", "21.9°");
                list.add(t);
                t = new Temperature("Dummy2", "22.9°");
                list.add(t);
                t = new Temperature("Dummy3", "23.9°");
                list.add(t);
                t = new Temperature("Dummy4", "24.9°");
                list.add(t);
                Collections.shuffle(list, new Random(seed));

                mTempListMutableLiveData.setValue(list);
            }
        }, 5000);

    }

    public MutableLiveData<String> getCurrentName() {
        if (mCurrentName == null) {
            mCurrentName = new MutableLiveData<String>();
        }
        return mCurrentName;
    }
}
