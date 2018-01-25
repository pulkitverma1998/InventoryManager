package com.example.android.inventorymanager;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.inventorymanager.data.InventoryContract.ItemEntry;
import com.example.android.inventorymanager.data.ItemDbHelper;

public class ItemCursorAdapter extends CursorAdapter {

    private ItemDbHelper itemDbHelper;

    private ItemCursorAdapter adapter;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        ImageView image = (ImageView) view.findViewById(R.id.product_image);
        final ImageButton sellButton = (ImageButton) view.findViewById(R.id.sellButton);
        final int position = cursor.getPosition();

        int productNameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_PRODUCT_QUANTITY);
        Uri imageUri = Uri.parse(cursor.getString(cursor.getColumnIndex(ItemEntry.COLUMN_IMAGE)));

        String productName = cursor.getString(productNameColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        final int productQuantity = cursor.getInt(quantityColumnIndex);

        String productPriceString = String.valueOf(productPrice);
        String productQuantityString = String.valueOf(productQuantity);

        productNameTextView.setText(productName);
        priceTextView.setText("₹ " + productPriceString);
        quantityTextView.setText(productQuantityString);
        image.setImageURI(imageUri);

        itemDbHelper = new ItemDbHelper(context);

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cursor.moveToPosition(position);
                String previousValueString = quantityTextView.getText().toString();
                int previousValue;
                if (previousValueString.isEmpty()) {
                    return;
                } else if (previousValueString.equals("0")) {
                    return;
                } else {
                    previousValue = Integer.parseInt(previousValueString);
                }
                int updatedValue = previousValue - 1;
                quantityTextView.setText(String.valueOf(updatedValue));
                int id = cursor.getColumnIndex(ItemEntry._ID);
                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_PRODUCT_QUANTITY, updatedValue);
                Uri uri = ContentUris.withAppendedId(ItemEntry.CONTENT_URI, cursor.getLong(id));
                context.getContentResolver().update(uri, values, null,null);
            }
        });

    }

}
