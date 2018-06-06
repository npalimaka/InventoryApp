package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.android.inventoryapp.StoreContract.ProductEntry;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.result_text_view)
    TextView textView;

    private StoreDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        dbHelper = new StoreDbHelper(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        insertProduct();
        displayDatabaseInfo();
    }

    private void insertProduct() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "Test Product");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 20.99);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 46);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Best Supplier Ever");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, "654-64-22");

        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);
    }

    private void displayDatabaseInfo() {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] project = {
                ProductEntry.COLUMN_ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        Cursor cursor = db.query(ProductEntry.TABLE_NAME, project, null, null,
                null, null, null);

        try {
            //setting the query results to the textView
            textView.setText("The products table contains " + cursor.getCount() + " products.\n");
            while (cursor.moveToNext()) {
                textView.append("\n" +
                        cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_ID)) + ". " +
                        cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME)) + " " +
                        cursor.getDouble(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE)) + " " +
                        cursor.getInt(cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY)) + " " +
                        cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME)) + " " +
                        cursor.getString(cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER))
                );
            }
        } finally {
            cursor.close();
        }
    }
}
