package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.example.android.inventoryapp.data.StoreContract.ProductEntry;

public class ProductCursorAdapter extends CursorAdapter {

    @BindView(R.id.name)
    TextView name;
    @BindView(R.id.price)
    TextView price;
    @BindView(R.id.quantity)
    TextView quantity;
    @BindView(R.id.sale_button)
    Button saleButton;

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        String nameString = cursor.getString(cursor.getColumnIndexOrThrow(
                ProductEntry.COLUMN_PRODUCT_NAME));
        name.setText(nameString);
        String priceString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));
        if (TextUtils.isEmpty(priceString)) {
            priceString = context.getString(R.string.price_not_specified);
        }
        price.setText(priceString);
        String quantityString = cursor.getString(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        quantity.setText(quantityString);
        final long id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry.COLUMN_ID));
        final int productsOnStock = Integer.parseInt(quantityString);

        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) context;
                mainActivity.saleProduct(id, productsOnStock);
            }
        });
    }
}
