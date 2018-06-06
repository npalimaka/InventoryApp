package com.example.android.inventoryapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.StoreContract.ProductEntry;

/**
 * This class is responsible for connecting to SQLite, creating the db
 */
public class StoreDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "store.db";
    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ProductEntry.TABLE_NAME
            + " (" + ProductEntry.COLUMN_ID + " INTEGER PRIMARY KEY," + ProductEntry.COLUMN_PRODUCT_NAME
            + " TEXT," + ProductEntry.COLUMN_PRODUCT_PRICE + " REAL," + ProductEntry.COLUMN_PRODUCT_QUANTITY
            + " INTEGER," + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT," + ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER
            + " TEXT);";
    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    public StoreDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
