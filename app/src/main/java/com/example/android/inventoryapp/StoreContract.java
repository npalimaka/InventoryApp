package com.example.android.inventoryapp;

import android.provider.BaseColumns;

/**
 * This class represents the table and its columns
 */
public class StoreContract {

    public abstract class ProductEntry implements BaseColumns{

        public static final String TABLE_NAME = "products";
        public static final String COLUMN_ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "Product_name";
        public static final String COLUMN_PRODUCT_PRICE = "Price";
        public static final String COLUMN_PRODUCT_QUANTITY = "Quantity";
        public static final String COLUMN_SUPPLIER_NAME = "Supplier_Name";
        public static final String COLUMN_SUPPLIER_PHONE_NUMBER = "Supplier_Phone_Number";
    }
}
