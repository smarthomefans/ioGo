package com.example.nagel.io1.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.nagel.io1.R;
import com.example.nagel.io1.service.DataBus;
import com.example.nagel.io1.service.Events;
import com.example.nagel.io1.service.SocketService;
import com.example.nagel.io1.service.repository.State;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.AndroidInjection;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.vi_switch) Switch wSwitch;
    @BindView(R.id.textState) TextView wTextView;
    @BindView(R.id.fab) FloatingActionButton fab;
    @BindView(R.id.fab_airplane) FloatingActionButton fab_airplane;
    @BindView(R.id.fab_bike) FloatingActionButton fab_bike;
    @BindView(R.id.fab_car) FloatingActionButton fab_car;

    boolean isFABOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AndroidInjection.inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DataBus.getBus().register(this);

        Intent intent = new Intent(this, SocketService.class);
        startService(intent);

    }

    @OnClick(R.id.fab)
    public void onClickFab() {
        if(!isFABOpen){
            showFABMenu();
        }else{
            closeFABMenu();
        }
    }

    @OnClick(R.id.fab_airplane)
    public void onClickFabAirplane() {
        Toast.makeText(MainActivity.this,
                "Airplane started", Toast.LENGTH_SHORT).show();
        closeFABMenu();
    }

    @OnClick(R.id.fab_bike)
    public void onClickFabBike() {
        Toast.makeText(MainActivity.this,
                "Bike started", Toast.LENGTH_SHORT).show();
        closeFABMenu();
    }

    @OnClick(R.id.fab_car)
    public void onClickFabCar() {
        Toast.makeText(MainActivity.this,
                "Car started", Toast.LENGTH_SHORT).show();
        closeFABMenu();
    }

    private void showFABMenu(){
        isFABOpen=true;
        fab.setRotation(135);
        fab_airplane.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
        fab_bike.animate().translationY(-getResources().getDimension(R.dimen.standard_105));
        fab_car.animate().translationY(-getResources().getDimension(R.dimen.standard_155));
    }

    private void closeFABMenu(){
        isFABOpen=false;
        fab.setRotation(0);
        fab_airplane.animate().translationY(0);
        fab_bike.animate().translationY(0);
        fab_car.animate().translationY(0);
    }

    @Override
    public void onBackPressed() {
        if(!isFABOpen){
            super.onBackPressed();
        }else{
            closeFABMenu();
        }
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Subscribe
    public void onStateChange(final Events.StateChange event) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("onStateChange", event.getId() + " => " + event.getData());
                State state = new State(event.getId(), event.getData());
                wTextView.setText(event.getId() + " => " + state.val);
                if ("javascript.0.vi_switch".equals(event.getId())) {
                    JSONObject data = null;
                    try {
                        data = new JSONObject(event.getData());
                        wSwitch.setChecked(data.getBoolean("val"));
                    } catch(JSONException e){
                        Log.e("onStateChange", e.getMessage());
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, SocketService.class);
        stopService(intent);
        DataBus.getBus().unregister(this);
    }

    public void onClickSwitch(View v){
        String tmp = (wSwitch.isChecked()) ? "true" : "false";

        Events.SetState event = new Events.SetState();
        event.setId("javascript.0.vi_switch");
        event.setVal(tmp);
        DataBus.getBus().post(event);

        Log.d("onClickSwitch", "new value: " + tmp);

    }

    public void onClickShowFragment(View v){
        Intent i = new Intent(this, FragmentActivity.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this,"Settings", Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
