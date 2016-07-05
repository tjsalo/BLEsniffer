/*
 * Copyright (c) 2015, Salo IT Solutions, inc.
 */

/**
 * defines the database schema that contains information about Bluetooth devices.
 *
 * Timothy J. Salo, September 3, 2015.
 */


package com.saloits.android.blescanner.contentprovider;


import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class BleContract {


    /**
     * The following Uri formats are supported:
     *
     * content://com.saloits.android.blescanner.app/company/companyid=
     */

    public static final String CONTENT_AUTHORITY = "com.saloits.android.blescanner.app";

    public static final Uri BASE_URI_CONTENT =
            Uri.parse(ContentResolver. SCHEME_CONTENT + CONTENT_AUTHORITY);



    /* DeviceEntry */

    /**
     * is an inner class that defines the "devices" table.  A device represents a remote Bluetooth
     * device that has a unique MAC address.  That is, a remote physical device that transmits
     * multiple MAC addresses is treated as multiple Bluetooth devices.
     *
     * A device may transmit multiple kinds of Bluetooth Advertisement frames; for example, a
     * single Bluetooth device might alternately transmit iBeacon and EddyStone Advertisement
     * frames (Apple license restrictions notwithstanding).
     *
     */


    public static final class DeviceEntry implements BaseColumns {

        public static final String TABLE_NAME = "devices";

        /*
         * BluetoothAdapter
         * BluetoothDevice
         * MAC address  String
         * Manufacturer String
         * Last seen    long
         *
         */
    }
    /* CompanyIdEntry */

    /**
     * is an inner class that defines the "ccmpanyids" table.
     */

    public static final class CompanyIdEntry implements BaseColumns {

        public static final String TABLE_NAME = "companyids";

        public static final String COLUMMN_ID = "id";
        public static final String COLUMN_NAME = "name";
    }
}
