package com.dexter.ecommerce;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dexter.ecommerce.adapters.AdapterCart;
import com.dexter.ecommerce.databases.OrdersDataSource;
import com.dexter.ecommerce.models.OrderData;
import com.dexter.ecommerce.models.SettingData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.dexter.ecommerce.Constant.ProductImageDir;

public class ActivityProductDetail extends Activity {

    private final static String LOGTAG = "productdetail";

    SQLiteOpenHelper dbHelper;
    OrdersDataSource dataSource;
    public static List<OrderData> orders;
    AdapterCart adapterCart;

    public static final int CONNECTION_TIMEOUT = 10000;
    public static final int READ_TIMEOUT = 15000;

    ImageView imgPreview;
    TextView txtText, txtSubText;
    WebView txtDescription;
    Button btnAdd;
    ScrollView sclDetail;
    TextView txtAlert;

    // declare variables to store menu data
    String productName, productStatus, productDescription;
    ArrayList<String> productImages = new ArrayList<>();
    double productPrice;
    Integer productStock;
    long productId;
    String ProductDetailAPI;

    // addMyOrder price format
    DecimalFormat formatData = new DecimalFormat("#.##");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Product Detail");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        imgPreview = findViewById(R.id.imgPreview);
        txtText = findViewById(R.id.txtText);
        txtSubText = findViewById(R.id.txtSubText);
        txtDescription = findViewById(R.id.txtDescription);
        btnAdd = findViewById(R.id.btnAdd);
        //btnShare = (Button) findViewById(R.id.btnShare);
        sclDetail = findViewById(R.id.sclDetail);

        // get screen device width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int wPix = dm.widthPixels;
        int hPix = wPix / 2 + 50;

        // change menu image width and height
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wPix, hPix);
        imgPreview.setLayoutParams(lp);

        dataSource = new OrdersDataSource(this);

        // get menu id that sent from previous page
        Intent iGet = getIntent();
        productId = iGet.getLongExtra("productId", 0);
        productName = iGet.getStringExtra("productName");
        productPrice = iGet.getDoubleExtra("productPrice", 0.0);
        productStatus = iGet.getStringExtra("productStatus");
        productDescription = iGet.getStringExtra("productDesc");
        productStock = iGet.getIntExtra("productStock", 0);
        productImages = iGet.getStringArrayListExtra("productImages");

        for (int i = 0; i < 1; i++) {
            GlideApp.with(ActivityProductDetail.this)
                    .load(ProductImageDir + productImages.get(i))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imgPreview);

        }
        SettingData settingData = new SettingData();
        txtText.setText(productName);
        txtSubText.setText("Price : " + productPrice + " " + settingData.getCurrency() + "\n" + "Status : " + productStatus + "\n" + "Stock : " + productStock);
        txtDescription.loadDataWithBaseURL("", productDescription, "text/html", "UTF-8", "");
        txtDescription.setBackgroundColor(Color.parseColor("#e7e7e7"));

        // Menu detail API url
        ProductDetailAPI = Constant.ProductDetailAPI;
        ProductDetailAPI += "/" + productId + "?accesskey=" + Constant.AccessKey;

        // call asynctask class to request data from server
//        new AsyncFetchProduct().execute();

        // event listener to handle add button when clicked
        btnAdd.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                // show input dialog
                inputDialog();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.cart:
                // refresh action
                Intent iMyOrder = new Intent(ActivityProductDetail.this, ActivityCart.class);
                startActivity(iMyOrder);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; go home
                this.finish();
                overridePendingTransition(R.anim.open_main, R.anim.close_next);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

    // method to show number of order form
    void inputDialog() {

        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle(R.string.order);
        alert.setMessage(R.string.number_order);
        alert.setCancelable(false);
        final EditText edtQuantity = new EditText(this);
        int maxLength = 3;
        edtQuantity.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
        edtQuantity.setInputType(InputType.TYPE_CLASS_NUMBER);
        alert.setView(edtQuantity);

        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String temp = edtQuantity.getText().toString();
                int quantity;

                // when add button clicked add menu to order table in database
                if (!temp.equalsIgnoreCase("")) {
                    quantity = Integer.parseInt(temp);

                    OrderData order = new OrderData();
                    order.setId(productId);
                    order.setProductName(productName);
                    order.setQuantity(quantity);
                    order.setPrice((productPrice * quantity));


                    if (dataSource.isColumnExist(productId)) {
                        order = dataSource.updateMyOrder(order);
                        Log.i(LOGTAG, "Order updated with product id " + order.getId());
                        Toast.makeText(ActivityProductDetail.this, "Cart Updated", Toast.LENGTH_SHORT).show();
                    } else {
                        order = dataSource.addMyOrder(order);
                        Toast.makeText(ActivityProductDetail.this, order.getProductName() + " added to cart", Toast.LENGTH_SHORT).show();
                        Log.i(LOGTAG, "Order created with product id " + order.getId());
                    }
                } else {
                    dialog.cancel();
                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // when cancel button clicked close dialog
                dialog.cancel();
            }
        });

        alert.show();
    }

    private class AsyncFetchProduct extends AsyncTask<String, String, String> {
        HttpURLConnection conn;
        URL url = null;
        ProgressDialog pdLoading = new ProgressDialog(ActivityProductDetail.this);
        private long startTime;
        private long stoptTime;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();


            //this method will be running on UI thread
            pdLoading.setMessage("\tLoading...");
            pdLoading.setCancelable(false);

            pdLoading.show();
            startTime = System.currentTimeMillis();

        }

        @Override
        protected String doInBackground(String... strings) {

            try {

                url = new URL(ProductDetailAPI);

            } catch (MalformedURLException e) {
                e.printStackTrace();
                return e.toString();
            }
            try {

                // Setup HttpURLConnection class to send and receive data from php and mysql
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(READ_TIMEOUT);
                conn.setConnectTimeout(CONNECTION_TIMEOUT);
                conn.setRequestMethod("GET");

            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                            ProductDetailAPI, null, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {

                            try {

                                JSONObject product = response.getJSONObject("data");
                                if (product.has("name"))
                                    productName = product.getString("name");

                                if (product.has("price"))
                                    productPrice = Double.valueOf(formatData.format(product.getDouble("price")));

                                if (product.has("description"))
                                    productDescription = product.getString("description");

                                if (product.has("stock"))
                                    productStock = Integer.valueOf(product.getString("stock"));

                                if (product.has("status"))
                                    productStatus = Integer.parseInt(product.getString("status")) == 1 ? "Available" : "Not Available";


                                if (product.has("images")) {
                                    JSONArray images = product.getJSONArray("images");
                                    for (int j = 0; j < images.length(); j++) {
                                        JSONObject imageData = images.getJSONObject(j);
                                        productImages.add(imageData.getString("name"));
                                    }
                                }

                                for (int i = 0; i < 1; i++) {
                                    GlideApp.with(ActivityProductDetail.this)
                                            .load(ProductImageDir + productImages.get(i))
                                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                                            .into(imgPreview);

                                }
                                txtText.setText(productName);
                                txtSubText.setText("Price : " + productPrice + " " + SettingData.getCurrency() + "\n" + "Status : " + productStatus + "\n" + "Stock : " + productStock);
                                txtDescription.loadDataWithBaseURL("", productDescription, "text/html", "UTF-8", "");
                                txtDescription.setBackgroundColor(Color.parseColor("#e7e7e7"));

                            } catch (JSONException e) {
                                Toast.makeText(ActivityProductDetail.this, e.toString(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        }

                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            error.printStackTrace();
                        }
                    });
                    Log.d(LOGTAG, "Product Request: " + request.toString());
                    Volley.newRequestQueue(ActivityProductDetail.this).add(request);

                } else {
                    Log.d(LOGTAG, "Http Error: " + response_code);
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pdLoading.dismiss();
        }
    }


    // close database before back to previous page
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        imageLoader.clearCache();
        super.onDestroy();
        dataSource.close();
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
