/*
 * Copyright (C) 2015, Salo IT Solutions, Inc.
 */

/**
 * provides utility functions that manipulate the bluetooth database.
 *
 * Timothy J. Salo, August 13, 2015.
 */


package com.saloits.android.blescanner.contentprovider;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


public class BleDbUtilities {

    private final String LOG_TAG = "******".concat(BleDbUtilities.class.getSimpleName());
    private final boolean DEBUG = true;


    /**
     * prints to the logcat file the contents of the SQLiteDatabase.
     *
     * The database is open in read mode, and then closed at completion.
     *
     * @param context context of database to dump.
     */

    public void dumpDbTables(Context context) {

        SQLiteDatabase db = new BleDbHelper(context).getReadableDatabase();

        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        int rowCount = c.getCount();

//        Log.d(LOG_TAG, "dummpDbTables, columns: "
//                        + c.getColumnCount()
//                        + ", count: "
//                        + c.getCount()
//        );
//
//        for (int i = 0; i < rowCount; i++) {
//            c.moveToPosition(i);
//            Log.d(LOG_TAG, c.getString(0));
//        }

        for (int i = 1; i < rowCount; i++) {
            c.moveToPosition(i);
            dumpTable(context, db, c.getString(0));
        }

        c.close();

        db.close();

    }


    /**
     * dumps the contents of a SQLiteDatabase table to the logcat file.
     *
     * The SQLiteDatabase is expected to be open when this method is called; this method does not
     * close the database.
     *
     * @param context context of database
     * @param db SQLiteDatabase
     * @param table table to dump
     */

    public void dumpTable(Context context, SQLiteDatabase db, String table) {

        Cursor c = db.rawQuery("SELECT * FROM " + table, null);
        int columnCount = c.getColumnCount();
        int rowCount = c.getCount();

        Log.d(LOG_TAG, "tabel: " + table + ", columns: " + columnCount + ", rows: " + rowCount);

        String[] columns = c.getColumnNames();
        String columnNames = "columns: ";
        for (int i = 0; i < columnCount; i++) {
            columnNames = columnNames + " " + columns[i];
        }

        c.moveToFirst();
        while (!c.isAfterLast()) {

            String columnValues = "values: ";
            for (int i = 0; i < columnCount; i++) {
                int colType = c.getType(i);
                if (i > 0) columnValues += ", '";

                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_NULL:
                        columnValues += "null";
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        columnValues += c.getLong(i);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        columnValues += c.getFloat(i);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        columnValues += c.getString(i);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        columnValues += "blob";
                        break;
                }
            }
            Log.d(LOG_TAG, columnValues);
            c.moveToNext();
        }
    }


    /**
     * dumps the contents of a Cursor to the logcat file.
     *
     * On exit, the Cursor is left at the same position as on entry.
     *
     * @param c Cursor to dump.
     */

    public void dumpCursor (Cursor c) {

        int columnCount = c.getColumnCount();
        int rowCount = c.getCount();
        int restorePosition = c.getPosition();

        String[] columns = c.getColumnNames();
        String columnNames = "columns: ";
        for (int i = 0; i < columnCount; i++) {
            columnNames = columnNames + " " + columns[i];
        }

        Log.d(LOG_TAG, columnNames);

        c.moveToFirst();
        while (!c.isAfterLast()) {

            String columnValues = "values: ";
            for (int i = 0; i < columnCount; i++) {
                int colType = c.getType(i);
                if (i > 0) columnValues += ", ";

                switch (c.getType(i)) {
                    case Cursor.FIELD_TYPE_NULL:
                        columnValues += "null";
                        break;
                    case Cursor.FIELD_TYPE_INTEGER:
                        columnValues += c.getLong(i);
                        break;
                    case Cursor.FIELD_TYPE_FLOAT:
                        columnValues += c.getFloat(i);
                        break;
                    case Cursor.FIELD_TYPE_STRING:
                        columnValues += c.getString(i);
                        break;
                    case Cursor.FIELD_TYPE_BLOB:
                        columnValues += "blob";
                        break;
                }
            }
            Log.d(LOG_TAG, columnValues);
            c.moveToNext();
        }

        c.moveToPosition(restorePosition);
    }
}
