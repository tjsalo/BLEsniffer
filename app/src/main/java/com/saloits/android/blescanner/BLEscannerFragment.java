/*
 * Copyright (C) 2015 Salo IT Solutions, Inc.
 */

/**
 * scans for Bluetooth devices and populates a master view of discovered Bluetooth devices.
 * BleScannerFragment will scan for either Bluetooth Low Energy (BLE) or Bluetooth Classic
 * devices, depending on the current settings.  Discovery of BLE devices requires API level
 * 18+ (Android 4.3+, Jelly Bean) and a device that supports BLE.  The user may also specify that
 * the API level 21 BLE API is used to discover BLE devices, on API level 21+ devices.
 *
 * Timothy J. Salo, August 24, 2015.
 */

/* TODO
 *
 * Listen for ACTION_STATE_CHANGED broadcasts.
 */


package com.saloits.android.blescanner;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;


public class BLEscannerFragment extends Fragment {


    private final String LOG_TAG = "******".concat(BLEscannerFragment.class.getSimpleName());
    private final boolean DEBUG = true;

    public static final int REQUEST_ENABLE_BT = 1;

    Context             mContext;
    boolean             mDiscoverBle;                   // if BLE devices are to be discovered
    int                 mApiLevel;
    int                 mBleApiLevel;
    BluetoothManager    mBluetoothManager;
    BluetoothAdapter    mBluetoothAdapter;
    boolean             mScanEnabled;
    BleScanListAdapter  mBleScanListAdapter;            // ListAdapter
    BluetoothAdapter.LeScanCallback mLeScanCallback;    // BLE API 18 callback
    BluetoothLeScanner  mBluetoothLeScanner;            // BLE API 21 scanner
    ScanCallback        mBluetoothScanCallback;         // BLE API 21 callback
    TextView            mTextView;                      // hack
    String              mText;                          // hack


    public BLEscannerFragment() {

        if (DEBUG) Log.d(LOG_TAG, "BLEscannerFragment()");
    }



    /* onAttach(Activity) */

    @Override
    public void onAttach(Activity activity) {

        Log.d(LOG_TAG, "onAttach()");

        mContext = activity;

        super.onAttach(activity);
    }



    /* onCreate(Bundle) */

    @Override
    public void onCreate(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onCreate()");

        super.onCreate(savedInstanceState);

    }



    /* onCreateView(LayoutInflater, ViewGroup, Bundle) */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (DEBUG) Log.d(LOG_TAG, "onCreateView(LayoutInflater, ViewGroup, Bundle)");

        return inflater.inflate(R.layout.fragment_ble_scanner, container, false);
    }



    /* onActivityCreated(Bundle) */

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onActivityCreated()");

        super.onActivityCreated(savedInstanceState);
    }



    /* onViewStateRestored(Bundle) */

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {

        Log.d(LOG_TAG, "onViewStateRestored()");

        super.onViewStateRestored(savedInstanceState);
    }



    /* onResume() */

    @Override
    public void onResume() {

        Log.d(LOG_TAG, "onResume()");

        super.onResume();
    }



    /* onStart() */

    /**
     * displays initial information about Bluetooth adapter and capabilities.
     */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onStart() {

        if (DEBUG) Log.d(LOG_TAG, "onStart()");

        super.onStart();

        /* Check Android API level, BLE API level preference. */

        mApiLevel = Build.VERSION.SDK_INT;

        /* Get settings. */

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        mBleApiLevel = Integer.parseInt(sharedPref.getString(getString(R.string.pref_api),
                getString(R.string.pref_api_default)));
        mDiscoverBle = sharedPref.getString(getString(R.string.pref_type),
                getString(R.string.pref_type_default))
                .equals(getString(R.string.pref_type_value_ble));

        Log.d(LOG_TAG, "API level: " + mBleApiLevel + ", BLE scan: " + mDiscoverBle);

        TextView textView = (TextView) getActivity().findViewById(R.id.main_textview);
        String text = "Running API level " + mApiLevel + "; Using BLE API level " + mBleApiLevel;
        textView.setText(text);

        /* Check if device has BLE-capable hardware. */

        if (!getActivity().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported,
                    Toast.LENGTH_LONG)
                    .show();
            return;
        }

        /* Check if device supports BLE API.*/

        if (mApiLevel < 18) {
            Toast.makeText(mContext, R.string.ble_api_not_supported,
                    Toast.LENGTH_LONG).show();
            return;
        }

        /* Check if BLE API level 21 desired and available. */

        if ((mBleApiLevel == 21) && (mApiLevel < 21)) {
            Toast.makeText(mContext, R.string.ble_api_downgraded,
                    Toast.LENGTH_LONG).show();

            mBleApiLevel = 18;
            text = "Running API level " + mApiLevel + "; Using BLE API level " + mBleApiLevel;
            textView.setText(text);
        }

        /* Get a BluetoothManager. */

        mBluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);

        /* See how many devices exist. */

        List<BluetoothDevice> bluetoothGattDeviceList =
                mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
//        text += "\nBluetooth GATT devices: " + bluetoothGattDeviceList.size();
//        textView.setText(text);

        List<BluetoothDevice> bluetoothServerDeviceList =
                mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT_SERVER);
//        text += "\nBluetooth GATT server devices: " + bluetoothServerDeviceList.size();
//        textView.setText(text);

        /*Get a BlueTooth Adapter. */

        mBluetoothAdapter = mBluetoothManager.getAdapter();

        String localBtAddr = mBluetoothAdapter.getAddress();
        String localBtName = mBluetoothAdapter.getName();

        text += "\nLocal Bluetooth address: " + localBtAddr;
        textView.setText(text);

        /* Check if Bluetooth enabled. */

        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        /* Scan for BLE devices. */

        Handler mHandler = new Handler();
        long SCAN_TIME = 30*1000;       // scan for 30 seconds
        mScanEnabled = true;            //  enable BLE scanning

        textView.setText(text + "\nScanning for BLE devices...\nScan Results:");
        mTextView= textView;
        mText = text;

        /* set ListAdapter */

        mBleScanListAdapter = new BleScanListAdapter(mContext);
        ListView listView = (ListView) getActivity().findViewById(R.id.main_listview);
        listView.setAdapter(mBleScanListAdapter);

        switch (mBleApiLevel) {

            /* Scan for BLE devices using BLE API 18. */

            case 18:

                /* Kill scan when scan time limit has expired. */

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanEnabled = false;
                        mTextView.setText(mText + "\nBLE scan complete\nScan Results:");
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, SCAN_TIME);

                /* Start BLE device scan. */

                if (DEBUG) Log.d(LOG_TAG, "calling startLeScan()");

                mLeScanCallback = new BleScanCallback18();
                mBluetoothAdapter.startLeScan(mLeScanCallback);

                break;

            /* Scan for BLE devices using BLE API level 21. */

            case 21:

                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
                mBluetoothScanCallback = new BleScanCallback21();

                /* Kill scan when scan time limit has expired. */

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanEnabled = false;
                        mTextView.setText(mText + "\nBLE scan complete\nScan Results:");
                        mBluetoothLeScanner.stopScan(mBluetoothScanCallback);
                    }
                }, SCAN_TIME);

                mBluetoothScanCallback = new BleScanCallback21();
                mBluetoothLeScanner.startScan(mBluetoothScanCallback);

            default:
        }



        final BroadcastReceiver mReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                // When discovery finds a device
                if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                    // Get the BluetoothDevice object from the Intent
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    // Add the name and address to an array adapter to show in a ListView
                    Log.d(LOG_TAG, "onReceiv() device: " + device.toString());
                }
            }
        };
        // Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mContext.registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
    }



    /* onPause() */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPause() {

        Log.d(LOG_TAG, "onPause()");

        super.onPause();

        /* Halt scan, if active. */

        if (mScanEnabled) {

            switch(mBleApiLevel) {
                case 18:
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    break;
                case 21:
                    mBluetoothLeScanner.stopScan(mBluetoothScanCallback);
                    break;
                default:
                    Log.d(LOG_TAG, "onStop(): lost...");
            }
            mScanEnabled = false;
            mTextView.setText(mText + "\nBLE scan complete\nScan Results:");
        }
    }



    /* OnStop() */

    @Override
    public void onStop() {

        Log.d(LOG_TAG, "onStop()");

        super.onStop();
    }



    /* ********************************************************************************************
     * BLE callback classes.
     */


    /* BleScanCallback18 */

    public class BleScanCallback18 implements BluetoothAdapter.LeScanCallback {

        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {

            final BleScanResult bleScanResult = new BleScanResult(18, device, rssi, scanRecord,
                    SystemClock.elapsedRealtimeNanos(), null);

            if (DEBUG) {

                Log.d(LOG_TAG, "\nonLeScan(BluetoothDevice, int, byte[])");
                Log.d(LOG_TAG, "device contents: "
                        + Integer.toHexString(device.describeContents()));
                Log.d(LOG_TAG, "device address: " + device.getAddress());
                Log.d(LOG_TAG, "device bound state: " + device.getBondState());
                Log.d(LOG_TAG, "device name: " + device.getName());
                Log.d(LOG_TAG, "device type: " + device.getType());
//                    Log.d(LOG_TAG, "toString(): " + device.toString());
                Log.d(LOG_TAG, "RSSI: " + rssi);
                Log.d(LOG_TAG, "Scan record length: " + scanRecord.length + "\n");
            }

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBleScanListAdapter.addBleScanResult(bleScanResult);
                    mBleScanListAdapter.notifyDataSetChanged();
                }
            });
        }

    }

    /* BleScanCallback21 */

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public class BleScanCallback21 extends ScanCallback {

        /* onScanFailed() */

        @Override
        public void onScanFailed(int errorCode) {

            switch (errorCode){
                case SCAN_FAILED_ALREADY_STARTED:
                    Log.d(LOG_TAG, "BleScanCallback21 scan failed: "
                            + "SCAN_FAILED_ALREADY_STARTED");
                    break;
                case SCAN_FAILED_APPLICATION_REGISTRATION_FAILED:
                    Log.d(LOG_TAG, "BleScanCallback21 scan failed: "
                            + "SCAN_FAILED_APPLICATION_REGISTRATION_FAILED");
                    break;
                case SCAN_FAILED_FEATURE_UNSUPPORTED:
                    Log.d(LOG_TAG, "BleScanCallback21 scan failed: "
                            + "SCAN_FAILED_FEATURE_UNSUPPORTED");
                    break;
                case SCAN_FAILED_INTERNAL_ERROR:
                    Log.d(LOG_TAG, "BleScanCallback21 scan failed: "
                            + "SCAN_FAILED_INTERNAL_ERROR");
                    break;
                default:
                    Log.d(LOG_TAG, "BleScanCallback21 scan failed: unknown reason");
            }

            super.onScanFailed(errorCode);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onScanResult(int callbackType, ScanResult result) {

            if (DEBUG) Log.d(LOG_TAG, "BleScanCallback21.onScanResult: callbackType: "
                    + callbackType + ", device: " + result.toString());

            final BleScanResult bleScanResult = new BleScanResult(21, null, 0, null, 0,
                    result);

            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mBleScanListAdapter.addBleScanResult(bleScanResult);
                    mBleScanListAdapter.notifyDataSetChanged();
                }
            });

            super.onScanResult(callbackType, result);
        }

    }
}
