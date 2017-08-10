package com.project.scanner.settings;

import android.content.Context;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.SwitchCompat;
import android.view.MotionEvent;
import android.view.View;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.project.scanner.R;

public class SettingsActivity extends AppCompatActivity {

    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        sharedPref = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file), Context.MODE_PRIVATE);

        SwitchCompat switchBeep = (SwitchCompat) findViewById(R.id.switch_beep);
        switchBeep.setChecked(sharedPref.getBoolean(getString(R.string.pref_beep), true));
        switchBeep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_beep), isChecked);
                editor.commit();
            }
        });

        SwitchCompat switchVibrate = (SwitchCompat) findViewById(R.id.switch_vibrate);
        switchVibrate.setChecked(sharedPref.getBoolean(getString(R.string.pref_vibrate), true));
        switchVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_vibrate), isChecked);
                editor.commit();
            }
        });

        SwitchCompat switchAddHistory = (SwitchCompat) findViewById(R.id.switch_add_to_history);
        switchAddHistory.setChecked(sharedPref.getBoolean(getString(R.string.pref_add_to_history), true));
        switchAddHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean(getString(R.string.pref_add_to_history), isChecked);
                editor.commit();
            }
        });

        //SwitchCompat switchAllowCamera = (SwitchCompat) findViewById(R.id.switch_allow_camera_access);
//        switchAllowCamera.setChecked(sharedPref.getBoolean(getString(R.string.pref_allow_camera_access), true));
//        switchAllowCamera.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
//        {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
//            {
//                SharedPreferences.Editor editor = sharedPref.edit();
//                editor.putBoolean(getString(R.string.pref_allow_camera_access), isChecked);
//                editor.commit();
//            }
//        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public void onBackPressed() {
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
//            super.onBackPressed();
//        }
//    }
}
