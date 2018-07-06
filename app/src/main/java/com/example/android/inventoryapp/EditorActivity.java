package com.example.android.inventoryapp;

import android.Manifest;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import com.example.android.inventoryapp.data.StoreContract.ProductEntry;

/**
 * Activity responsible for editing and adding a product
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCT_LOADER = 0;
    @BindView(R.id.edit_name)
    EditText nameEditText;
    @BindView(R.id.edit_price)
    EditText priceEditText;
    @BindView(R.id.edit_quantity)
    EditText quantityEditText;
    @BindView(R.id.supplier_name)
    EditText supplierEditText;
    @BindView(R.id.edit_phone)
    EditText phoneEditText;
    @BindView(R.id.delete_button)
    ImageButton deleteButton;
    @BindView(R.id.delete_label)
    TextView deleteLabel;
    private Uri currentProductUri;
    private boolean productHasChanged;
    private boolean providedInvalidDetails;
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            productHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        ButterKnife.bind(this);

        //check if new or existing product uri from MainActivity
        Intent intent = getIntent();
        currentProductUri = intent.getData();
        if (currentProductUri == null) {
            setTitle(getString(R.string.add_product));
            //hiding the delete button since it is a new product
            deleteLabel.setVisibility(View.INVISIBLE);
            deleteButton.setVisibility(View.INVISIBLE);
        } else {
            setTitle(getString(R.string.edit_product));
            getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);
        }

        //setting a listener for unsaved changes notification
        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierEditText.setOnTouchListener(touchListener);
        phoneEditText.setOnTouchListener(touchListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.editor_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveProduct();
                if (!providedInvalidDetails) {
                    finish();
                }
                return true;
            case android.R.id.home:
                if (!productHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies about unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProduct() {
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierString = supplierEditText.getText().toString().trim();
        String phoneString = phoneEditText.getText().toString().trim();

        /* check for invalid details
        * no need to check price since it can be null
        * setting default quantity
        */
        int quantityInt = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantityInt = Integer.parseInt(quantityString);
        }
        if (currentProductUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                quantityInt == 0 && TextUtils.isEmpty(supplierString) &&
                TextUtils.isEmpty(phoneString)) {
            return;
        } else if (TextUtils.isEmpty(nameString)) {
            providedInvalidDetails = true;
            Toast.makeText(this, getString(R.string.valid_name), Toast.LENGTH_SHORT).show();
            return;
        } else  if(TextUtils.isEmpty(supplierString)){
            providedInvalidDetails = true;
            Toast.makeText(this, getString(R.string.valid_supplier), Toast.LENGTH_SHORT).show();
            return;
        } else if(TextUtils.isEmpty(phoneString)){
            providedInvalidDetails = true;
            Toast.makeText(this, getString(R.string.valid_phone), Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantityInt);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierString);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER, phoneString);

        //in case saving a new product, new uri created
        if (currentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);
            if (newUri == null) {
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
                providedInvalidDetails = false;
            }
            //in case updating an existing product current uri used
        } else {
            int rowsAffected = getContentResolver().update(currentProductUri, values, null, null);
            if (rowsAffected == 0) {
                Toast.makeText(this, getString(R.string.editor_update_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_update_product_successful),
                        Toast.LENGTH_SHORT).show();
                providedInvalidDetails = false;
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if (currentProductUri != null) {
            String[] projection = {
                    ProductEntry.COLUMN_ID,
                    ProductEntry.COLUMN_PRODUCT_NAME,
                    ProductEntry.COLUMN_PRODUCT_PRICE,
                    ProductEntry.COLUMN_PRODUCT_QUANTITY,
                    ProductEntry.COLUMN_SUPPLIER_NAME,
                    ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

            return new CursorLoader(
                    this,
                    currentProductUri,
                    projection,
                    null,
                    null,
                    null);
        } else {
            return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) {
            return;
        }

        if (data.moveToFirst()) {
            int nameColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int phoneColumnIndex = data.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String name = data.getString(nameColumnIndex);
            int price = data.getInt(priceColumnIndex);
            int quantity = data.getInt(quantityColumnIndex);
            String supplier = data.getString(supplierColumnIndex);
            String phone = data.getString(phoneColumnIndex);

            nameEditText.setText(name);
            priceEditText.setText(String.valueOf(price));
            quantityEditText.setText(String.valueOf(quantity));
            supplierEditText.setText(supplier);
            phoneEditText.setText(phone);
        }
    }

    //clearing all the views
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierEditText.setText("");
        phoneEditText.setText("");
    }

    //shows notification about unsaved changes with discard and accept options
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //setting the notification
    @Override
    public void onBackPressed() {
        if (!productHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @OnClick(R.id.delete_button)
    public void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {
        if (currentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    @OnClick(R.id.decrement)
    public void decrementPrice() {
        int quantity = Integer.parseInt(quantityEditText.getText().toString().trim());
        if (quantity > 0) {
            quantity--;
        }
        quantityEditText.setText(String.valueOf(quantity));
    }

    @OnClick(R.id.increment)
    public void incrementPrice() {
        String quantityString = quantityEditText.getText().toString().trim();
        int quantity = 0;
        if (!TextUtils.isEmpty(quantityString)) {
            quantity = Integer.parseInt(quantityString);
        }
        quantity++;
        quantityEditText.setText(String.valueOf(quantity));
    }

    @OnClick(R.id.call_button)
    public void callTheSupplier() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneEditText.getText().toString().trim()));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            Log.v(EditorActivity.class.getName(), getString(R.string.call_not_permitted));
            startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneEditText.getText().toString().trim())));
            return;
        }
        startActivity(intent);
    }
}
