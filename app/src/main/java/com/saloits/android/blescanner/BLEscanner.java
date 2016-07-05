/*
 * Copyright (C) 2015 Salo IT Solutions, Inc.
 */

/**
 * scans for Bluetooth Low Energy (BLE) and Bluetooth devices.
 *
 * Requires API level 18 (Android 4.3, Jelly Bean) and a device that supports BLE.  BLEscanner
 * works much better with at least API 21 (Android 5.0, Lollipop).
 *
 * Timothy J. Salo, August 24, 2015.
 */


package com.saloits.android.blescanner;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.saloits.android.blescanner.contentprovider.BleDbHelper;


public class BLEscanner extends AppCompatActivity {


    private final String LOG_TAG = "******".concat(BLEscanner.class.getSimpleName());
    private final boolean DEBUG = true;

    /* onCreate(Bundle) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Log.d (LOG_TAG, "onCreate()");

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ble_scanner);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        SQLiteDatabase db = new BleDbHelper(this).getReadableDatabase();


    }



    /* onCreateOptionsMenu(Menu) */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if(DEBUG) Log.d(LOG_TAG, "onCreateOptionsMenu()");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ble_scanner, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        int id = item.getItemId();

        if (DEBUG) Log.d(LOG_TAG, "onOptionsItemSelected, itemId: " + id);

        if (id == R.id.action_settings) {

            Log.d(LOG_TAG, "\"Settings\" button pushed.");

            Intent preferencesActivityIntent =
                    new Intent(this, BlePreferences.class);

            if (preferencesActivityIntent.resolveActivity(
                    getPackageManager()) != null) {
                startActivity(preferencesActivityIntent);
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
