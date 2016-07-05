/*
 * Copyright (C) 2015, Salo IT Solutions, Inc.
 */

/**
 * permits the user to change BLEscanner preferences.
 *
 * Timothy J. Salo, August 30, 2015.
 */


package com.saloits.android.blescanner;


import android.os.Bundle;
import android.preference.PreferenceFragment;


public class BlePreferencesFragment extends PreferenceFragment {


    public BlePreferencesFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.preferences);
    }
}
