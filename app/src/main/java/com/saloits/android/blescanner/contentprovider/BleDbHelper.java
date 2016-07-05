/*
 * Copyright (C) 2015, Salo IT Solutions, Inc.
 */

/**
 * maintains database of Bluetooth device information.
 *
 * Timothy J. Salo, September 3, 2015.
 */


package com.saloits.android.blescanner.contentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.saloits.android.blescanner.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Vector;

public class BleDbHelper extends SQLiteOpenHelper {

    private final String LOG_TAG = "******".concat(BleDbHelper.class.getSimpleName());
    private final boolean DEBUG = true;

    private static final int DATABASE_VERSION = 5;
    static final String DATABASE_NAME = "ble.db";

    Context mContext;



    /* BleDbHelper(Context) */

    public BleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        mContext = context;
    }



    /* onCreate(SQLiteDatabase) */

    @Override
    public void onCreate(SQLiteDatabase db) {

        /* Create "companids" table. */

        final String SQL_CREATE_COMPANYID_TABLE = "CREATE TABLE "
                + BleContract.CompanyIdEntry.TABLE_NAME
                + " ("
                + BleContract.CompanyIdEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + BleContract.CompanyIdEntry.COLUMMN_ID + " LONG NOT NULL UNIQUE, "
                + BleContract.CompanyIdEntry.COLUMN_NAME + " STRING "
                + ");";

        if (DEBUG) Log.d(LOG_TAG, "onCreate(): ".concat(SQL_CREATE_COMPANYID_TABLE));

        db.execSQL(SQL_CREATE_COMPANYID_TABLE);

        /* Populate Bluetooth Company ids table. */

        InputStream inputStream = mContext.getResources().openRawResource(R.raw.bluetooth_company_ids);

        if (inputStream != null){

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//            Vector<ContentValues> cVVector = new Vector<ContentValues>();

            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] tokens = line.split(",",3);
                    String out = "";
                    for (int i = 0; i < tokens.length; i++) {
                        out += tokens[i];
                        if (i < tokens.length-i ) out += " ; ";
                    }

                    int companyId = Integer.parseInt(tokens[0]);
                    if (companyId != 0) {
                        ContentValues companyIdValues = new ContentValues();
                        companyIdValues.put(BleContract.CompanyIdEntry.COLUMMN_ID, companyId);
                        tokens[2] = tokens[2].replaceAll("\"", "");
                        companyIdValues.put(BleContract.CompanyIdEntry.COLUMN_NAME, tokens[2]);
//                        cVVector.add(companyIdValues);
                        db.insert(BleContract.CompanyIdEntry.TABLE_NAME, null, companyIdValues);
                    }
                }
            } catch (IOException e) {
                Log.d(LOG_TAG, "Reading Bluetooth company ids: readLine() failed");
            }

            new BleDbUtilities().dumpTable(mContext, db, BleContract.CompanyIdEntry.TABLE_NAME);
        }

    }



    /* onUpgrade() */

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(DEBUG) Log.d(LOG_TAG, "onUpgrade()");

        db.execSQL("DROP TABLE IF EXISTS " + BleContract.CompanyIdEntry.TABLE_NAME);
        onCreate(db);

    }


    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        onUpgrade(db, oldVersion, newVersion);
    }
}
