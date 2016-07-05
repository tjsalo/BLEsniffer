/*
 * Copyright (C) 2015, Salo IT Solutions, Inc.
 */

/*
 * holds the results of a BLE scan performed by either the API 18 or the API 21 Bluetooth LE
 * scan.
 *
 * Timothy J. Salo, August 29, 2015.
 */

package com.saloits.android.blescanner;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanResult;
import android.os.Build;

public class BleScanResult {

    int             mBleApiLevel;        // BLE API level

    /* BLE API level 18 variables. */

    BluetoothDevice mBluetoothDevice;   // Bluetooth LE device
    int             mRssi;              // RSSI
    byte[]          mScanRecord;        // API 18 raw bytes
    long            mTimestampNanos;    // timestamp

    /* BLE API level 21 variables */

    ScanResult      mScanResult;        // API 21-style ScanResult



    /* BleScanResult(int, BluetoothDevice, byte[], int */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    BleScanResult(int bleApiLevel, BluetoothDevice device, int rssi, byte[] scanRecord,
                  long timestampNanos, ScanResult scanResult) {

        mBleApiLevel = bleApiLevel;     // record BLE API level

        switch (mBleApiLevel) {

            case 18:
                mBluetoothDevice = device;
                mScanRecord = scanRecord;
                mRssi = rssi;
                mTimestampNanos = timestampNanos;
                break;

            case 21:
                mScanResult = scanResult;
                mBluetoothDevice = mScanResult.getDevice();
                mScanRecord = mScanResult.getScanRecord().getBytes();
                mRssi = mScanResult.getRssi();
                mTimestampNanos = mScanResult.getTimestampNanos();
                break;

            default:
                // we're lost...
                break;
        }

    }


    int getBleApiLevel() {
        return mBleApiLevel;
    }

    BluetoothDevice getDevice() {
        return mBluetoothDevice;
    }

    int getRssi() {

        return mRssi;
    }

    byte[] getScanRecord() {
        return mScanRecord;
    }

    long getTimestampNanos() {
        return mTimestampNanos;
    }

    ScanResult getScanResult() {
        return mScanResult;
    }

}
