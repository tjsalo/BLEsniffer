/*
 * Copyright (C) 2015, Salo IT Solutions, Inc.
 */

/*
 * ListAdapter for master scanner result screen.
 *
 * Timothy J. Salo, August 30, 2015.
 */


package com.saloits.android.blescanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class BleScanListAdapter implements ListAdapter {


    private static final int VIEW_TYPE_18 = 0;
    private static final int VIEW_TYPE_21 = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    private final String LOG_TAG = "******".concat(BleScanListAdapter.class.getSimpleName());
    private final boolean DEBUG = true;

    ArrayList<BleScanResult>    mBleScanResultList;
    DataSetObserver             mDataSetObserver;
    Context                     mContext;
    int                         mViewType;


    /* Null constructor. */

    BleScanListAdapter() {
        mBleScanResultList = new ArrayList<BleScanResult>();
    }


    /* BleSccanListAdapter(Context) */

    BleScanListAdapter(Context context) {
        mContext = context;
        mBleScanResultList = new ArrayList<BleScanResult>();
    }


    /* getItemId(int) */

    @Override
    public long getItemId(int position) {
        return position;
    }


    /* hasStableIds() */

    @Override
    public boolean hasStableIds() {
        return false;
    }


    /* getItem(int) */

    @Override
    public Object getItem(int position) {
        return mBleScanResultList.get(position);
    }


    /* registerDataSetObserver(DataSetObserver) */

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDataSetObserver = observer;
    }


    /* unregisterDataSetObserver(DataSetObserver) */

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDataSetObserver = null;
    }


    /* isEmpty() */

    @Override
    public boolean isEmpty() {
        return mBleScanResultList.isEmpty();
    }


    /* getCount() */

    @Override
    public int getCount() {
        return mBleScanResultList.size();
    }


    /* getView(int, View, ViewGroup) */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        BleScanResult bleScanResult = mBleScanResultList.get(position);

        int bleApiLevel = bleScanResult.getBleApiLevel();
        View view = null;
        BluetoothDevice bluetoothDevice;
        int rssi;
        String hexString;
        byte[] scanRecord;

        switch (bleApiLevel) {

            /* Display results of BLE API level 18 scan. */

            case 18:
                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_18, parent, false);

                bluetoothDevice = bleScanResult.getDevice();
                rssi = bleScanResult.getRssi();
                scanRecord = bleScanResult.getScanRecord();

                TextView textView181 = (TextView) view.findViewById(R.id.listview_textview_18_1);
                textView181.setText(bluetoothDevice.getAddress()
                        + " (" + bluetoothDevice.getName() + ")");

                TextView textView182 = (TextView) view.findViewById(R.id.listview_textview_18_2);
                textView182.setText("RSSI: " + rssi);

                hexString = "";

                for (int i = 0; i < scanRecord.length; i++) {
                    hexString += byteToHex(scanRecord[i]) + " ";
                }

                if (DEBUG) Log.d(LOG_TAG, hexString);

                TextView textView183 = (TextView) view.findViewById(R.id.listview_textview_18_3);
                textView183.setText(hexString);

                break;

            /* Display results of BLE API level 21 scan. */

            case 21:

                view = LayoutInflater.from(mContext).inflate(R.layout.list_item_21, parent, false);

                ScanResult scanResult = bleScanResult.getScanResult();
                bluetoothDevice = bleScanResult.getDevice();
                rssi = bleScanResult.getRssi();
                ScanRecord scanRecord21 = scanResult.getScanRecord();
                scanRecord = scanRecord21.getBytes();

                        TextView textView1 = (TextView) view.findViewById(R.id.listview_textview_21_1);
                textView1.setText(bluetoothDevice.getAddress() + " (" + bluetoothDevice.getName() + ")");

                TextView textView2 = (TextView) view.findViewById(R.id.listview_textview_21_2);

                String text212;

                /* RSSI. */

                text212 = "RSSI: " + rssi;

                /* Advertise Flags. */

                int advertiseFlags = scanRecord21.getAdvertiseFlags();
                text212 += "\nAdvertiseFlags: " + Integer.toHexString(advertiseFlags);

                /* Manufacturer Data. */

                text212 += "\nManufacturer data:";

                SparseArray<byte[]> manufacturerData = scanRecord21.getManufacturerSpecificData();
                for (int i = 0; i < manufacturerData.size(); i++) {
                    text212 += "\n  Manufacturer id: " + manufacturerData.keyAt(i);
                }

                textView2.setText(text212);

                String text3 = "";

                for (int i = 0; i < scanRecord.length; i++) {
                    text3 += byteToHex(scanRecord[i]) + " ";
                }

                if (DEBUG) Log.d(LOG_TAG, text3);

                TextView textView3 = (TextView) view.findViewById(R.id.listview_textview_21_3);
                textView3.setText(text3);
        }
        return view;
    }

    String byteToHex (byte b) {
        String byteToHex = "0123456789abcdef";
        char nibble1 = byteToHex.charAt((b >> 4) & 0x0f);
        char nibble2 = byteToHex.charAt((b & 0x0f));
        char[] hex = {nibble1, nibble2};
        return new String(hex);
    }


    /* getViewTypeCount() */

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }


    /* getItemViewType(int) */

    @Override
    public int getItemViewType(int position) {
        return VIEW_TYPE_18;
    }


    /* areAllItemsEnabled() */

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }


    /* isEnabled(int) */

    @Override
    public boolean isEnabled(int position) {
        return true;
    }


    /* addBleScanResult(BleScanResult) */

    public void addBleScanResult(BleScanResult bleScanResult) {
        mBleScanResultList.add(bleScanResult);
    }


    /* notifyDataSetChanged() */

    public void notifyDataSetChanged() {
        if(mDataSetObserver != null) mDataSetObserver.onChanged();
    }


}
