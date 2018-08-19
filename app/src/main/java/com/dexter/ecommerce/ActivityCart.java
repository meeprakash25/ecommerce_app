package com.dexter.ecommerce;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dexter.ecommerce.adapters.AdapterCart;
import com.dexter.ecommerce.databases.OrdersDataSource;
import com.dexter.ecommerce.models.OrderData;
import com.dexter.ecommerce.models.SettingData;

import java.text.DecimalFormat;
import java.util.List;

public class ActivityCart extends Activity {

    private final static String LOGTAG = "logtag";

    OrdersDataSource dataSource;
    public static List<OrderData> orders;

    // declare view objects
    ListView listOrder;
    TextView txtTotalLabel, txtTotal, txtAlert, lytTotal;
    Button btnClear, Checkout;

    AdapterCart adapterCart;


    // declare static variables to store tax and currency data
    public static double Tax = SettingData.getTax();
    public static String Currency = SettingData.getCurrency();

    double totalPrice;
    final int CLEAR_ALL_ORDER = 0;
    final int CLEAR_ONE_ORDER = 1;
    int FLAG;
    Long ID;
    String TaxCurrencyAPI;

    // addMyOrder price format
    DecimalFormat formatData = new DecimalFormat("#.##");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart_activity);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Order Detail");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        txtTotalLabel = findViewById(R.id.txtTotalLabel);
        txtTotal = findViewById(R.id.txtTotal);

        Checkout = findViewById(R.id.Checkout);
        listOrder = findViewById(R.id.listOrder);
        btnClear = findViewById(R.id.btnClear);

        dataSource = new OrdersDataSource(this);
        dataSource.open();
        orders = dataSource.findAll();
        if (orders.size() == 0) {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_LONG).show();
        } else {

            for (int i = 0; i < orders.size(); i++) {
                totalPrice += orders.get(i).getPrice();
            }

            totalPrice += (totalPrice * (Tax / 100));
            totalPrice = Double.parseDouble(formatData.format(totalPrice));

            txtTotal.setText(totalPrice + " " + Currency);
            txtTotalLabel.setText(getString(R.string.total_order) + " (Tax " + Tax + "%)");


            orders = dataSource.findAll();
            adapterCart = new AdapterCart(this, orders);
            listOrder.setAdapter(adapterCart);
        }

        // event listener to handle clear button when clicked
        btnClear.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // show confirmation dialog
                showClearDialog(CLEAR_ALL_ORDER, 1111L);
            }
        });

        // event listener to handle list when clicked
        listOrder.setOnItemClickListener(new OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
                // show confirmation dialog
                showClearDialog(CLEAR_ONE_ORDER, orders.get(position).getId());
            }
        });

        Checkout.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                if (orders.size() == 0) {
                    Toast.makeText(getApplicationContext(), "Add some items to the cart first before checkout", Toast.LENGTH_LONG).show();
                } else {
                    dataSource.close();
                    Intent intent = new Intent(ActivityCart.this, ActivityCheckout.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.open_next, R.anim.close_next);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // method to addMyOrder dialog
    void showClearDialog(int flag, Long id) {
        FLAG = flag;
        ID = id;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm);
        switch (FLAG) {
            case 0:
                builder.setMessage(getString(R.string.clear_all_order));
                break;
            case 1:
                builder.setMessage(getString(R.string.clear_one_order));
                break;
        }
        builder.setCancelable(false);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                switch (FLAG) {
                    case 0:
                        // clear all menu in order table
                        dataSource.deleteAllOrders();
                        listOrder.invalidateViews();
                        clearData();
                        recreate();
                        break;
                    case 1:
                        // clear selected menu in order table
                        dataSource.deleteAnOrder(ID);
                        listOrder.invalidateViews();
                        clearData();
                        recreate();
                        break;
                }

            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
                // close dialog
                dialog.cancel();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();

    }

    // clear arraylist variables before used
    void clearData() {
        orders.clear();
    }

    // method to get data from server
    public void getDataFromDatabase() {


    }

    // when back button pressed close database and back to previous page
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        super.onResume();
        dataSource.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        dataSource.close();
    }

}
