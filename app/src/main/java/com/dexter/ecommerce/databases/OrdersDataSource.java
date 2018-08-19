package com.dexter.ecommerce.databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dexter.ecommerce.models.OrderData;

import java.util.ArrayList;
import java.util.List;

public class OrdersDataSource {

    private final static String LOGTAG = "logtag";

    private SQLiteOpenHelper dbHelper;
    private SQLiteDatabase database;

    private static final String[] allColumns = {
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_PRODUCT_ID,
            DBHelper.COLUMN_PRODUCT_NAME,
            DBHelper.COLUMN_QUANTITY,
            DBHelper.COLUMN_TOTAL_PRICE
    };

    private static final String[] columnProductId = {
            DBHelper.COLUMN_PRODUCT_ID,
    };

    public OrdersDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() {
        Log.i(LOGTAG, "Database opened");
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        Log.i(LOGTAG, "Database closed");
        dbHelper.close();
    }

    /**
     * this code is used to get all data from database
     */
    public boolean isColumnExist(long productId) {

        Cursor cursor;

        String where = DBHelper.COLUMN_PRODUCT_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(productId)};
        String limit = "1";

        cursor = database.query(DBHelper.TABLE_ORDERS, columnProductId, where, whereArgs, null, null, null, limit);

        if (cursor.getCount() > 0) {
            return true;
        }

        cursor.close();

        return false;
    }

    public OrderData addMyOrder(OrderData order) {

        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_PRODUCT_ID, order.getId());
        values.put(DBHelper.COLUMN_PRODUCT_NAME, order.getProductName());
        values.put(DBHelper.COLUMN_QUANTITY, order.getQuantity());
        values.put(DBHelper.COLUMN_TOTAL_PRICE, order.getPrice());
        database.insert(DBHelper.TABLE_ORDERS, null, values);
        return order;
    }

    public OrderData updateMyOrder(OrderData order) {

        String where = DBHelper.COLUMN_PRODUCT_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(order.getId())};

        ContentValues values = new ContentValues();
//        values.put(DBHelper.COLUMN_PRODUCT_ID, order.getId());
        values.put(DBHelper.COLUMN_PRODUCT_NAME, order.getProductName());
        values.put(DBHelper.COLUMN_QUANTITY, order.getQuantity());
        values.put(DBHelper.COLUMN_TOTAL_PRICE, order.getPrice());
        Integer result = database.update(DBHelper.TABLE_ORDERS, values, where, whereArgs);

        Log.i(LOGTAG, result + " row affected");
        return order;
    }

    public void deleteAnOrder(Long id) {

        String where = DBHelper.COLUMN_ID + "=?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        Integer result = database.delete(DBHelper.TABLE_ORDERS, where, whereArgs);

        Log.i(LOGTAG, result + " row affected");
    }

    public void deleteAllOrders() {

        Integer result = database.delete(DBHelper.TABLE_ORDERS, null, null);
        Log.i(LOGTAG, result + " rows affected");
    }

    public List<OrderData> findAll() {
        List<OrderData> orders = new ArrayList<>();
        Cursor cursor = database.query(DBHelper.TABLE_ORDERS, allColumns, null, null, null, null, null);
        Log.i(LOGTAG, "Returned " + cursor.getCount() + " rows");

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                OrderData order = new OrderData();
                order.setId(cursor.getLong(cursor.getColumnIndex(DBHelper.COLUMN_ID)));
                order.setProductName(cursor.getString(cursor.getColumnIndex(DBHelper.COLUMN_PRODUCT_NAME)));
                order.setQuantity(cursor.getInt(cursor.getColumnIndex(DBHelper.COLUMN_QUANTITY)));
                order.setPrice(cursor.getDouble(cursor.getColumnIndex(DBHelper.COLUMN_TOTAL_PRICE)));
                orders.add(order);
            }
        }
        return orders;
    }
}
