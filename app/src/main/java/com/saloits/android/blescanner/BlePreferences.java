/*
 * Copyright (C) 2015, Salo IT Solutions, Inc.
 */

/**
 * permits the user to change the application settings.
 *
 * This code supports one setting: "
 *
 * Timothy J. Salo, August 30, 2015.
 */


package com.saloits.android.blescanner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;



public class BlePreferences extends PreferenceActivity {

    private final String LOG_TAG = "******".concat(BlePreferences.class.getSimpleName());
    private final boolean DEBUG = true;

    String mBlePref;
    SharedPreferences.OnSharedPreferenceChangeListener mListener;



    /* onCreate(Bundle) */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Save current display preference. */

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mBlePref =
                sharedPref.getString(getString(R.string.pref_api),
                        getString(R.string.pref_api_default));

        /* Initiate preferences fragment. */

        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new BlePreferencesFragment())
                .commit();
    }



    /* onCreateOptionsMenu(Menu) */

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_preferences, menu);
        return true;
    }



    /* onResume() */

    @Override
    protected void onResume() {
        super.onResume();

        mListener =
                new SharedPreferences.OnSharedPreferenceChangeListener() {
                    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                        if (key.equals(getString(R.string.pref_api))) {

                            String displayPref =
                                    prefs.getString(getString(R.string.pref_api),
                                            getString(R.string.pref_api_default));
                            if (DEBUG) Log.d(LOG_TAG, "on...Changed: Preference "
                                    + getString(R.string.pref_api) + " : " + displayPref);
                        }
                    }
                };
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(mListener);
        mListener = null;
    }



    /* onPause() */

    @Override
    protected void onPause() {
        super.onPause();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(mListener);

    }



    /* onOptionsItemSelected(MenuItem) */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

