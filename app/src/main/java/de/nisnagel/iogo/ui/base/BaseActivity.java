package de.nisnagel.iogo.ui.base;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import de.nisnagel.iogo.R;
import de.nisnagel.iogo.service.SocketService;
import de.nisnagel.iogo.ui.info.InfoActivity;
import de.nisnagel.iogo.ui.settings.SettingsActivity;


public class BaseActivity extends AppCompatActivity{

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, SocketService.class));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_action_settings) {
            Toast.makeText(this,R.string.menu_action_settings, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        if (id == R.id.menu_action_info) {
            Toast.makeText(this,R.string.menu_action_info, Toast.LENGTH_LONG).show();
            Intent i = new Intent(this, InfoActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
