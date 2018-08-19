package com.dexter.ecommerce;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dexter.ecommerce.adapters.AdapterProductList;
import com.dexter.ecommerce.models.ProductData;
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

public class ActivityProductList extends Activity {

    public static final String TAG = "ProductList";
    public ProgressDialog progressDialog;

    public String ProductAPI = Constant.ProductAPI;
    public String TaxCurrencyAPI = Constant.TaxCurrencyAPI;

    RecyclerView listProduct;
    RecyclerView.Adapter AdapterProductList;
    List<ProductData> ProductData = new ArrayList<>();

    RecyclerView.LayoutManager recyclerViewLayoutManager;

    String categoryId;
    String categoryName;

    // create price format
    DecimalFormat formatData = new DecimalFormat("#.##");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_list);

        // getIncomingIntent
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Product");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        // tax and currency API url
        TaxCurrencyAPI = TaxCurrencyAPI + "?accesskey=" + Constant.AccessKey;

        // get category id and category name that sent from previous page
        Intent intent = getIntent();
        categoryId = intent.getStringExtra("categoryId");
        categoryName = intent.getStringExtra("categoryName");
        ProductAPI += "/" + categoryId + "?accesskey=" + Constant.AccessKey;

        new AsyncFetchProduct().execute();

        listProduct = findViewById(R.id.listProduct);
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 1);
        listProduct.setLayoutManager(recyclerViewLayoutManager);
        AdapterProductList = new AdapterProductList(ActivityProductList.this, ProductData);
        listProduct.setAdapter(AdapterProductList);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_category, menu);
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
                Intent intent = new Intent(ActivityProductList.this, ActivityCart.class);
                startActivity(intent);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            case R.id.refresh:
                listProduct.invalidate();
                clearData();
                new AsyncFetchProduct().execute();
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

    //    fetch Tax Currency
    // private class AsyncFetchTaxCurrency extends AsyncTask<String, String, String> {
    //     @Override
    //     protected void onPreExecute() {
    //         super.onPreExecute();
    //     }
    //
    //     @Override
    //     protected String doInBackground(String... strings) {
    //         FetchTaxCurrency();
    //         return null;
    //     }
    // }
    //
    // void FetchTaxCurrency() {
    //
    //     JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
    //             TaxCurrencyAPI, null, new Response.Listener<JSONObject>() {
    //
    //         @Override
    //         public void onResponse(JSONObject response) {
    //
    //             SettingData settingData = new SettingData();
    //             try {
    //                 if (response.has("tax")) {
    //                     settingData.setTax(Double.parseDouble(response.getString("tax")));
    //                 }
    //
    //                 if (response.has("currency")) {
    //                     settingData.setCurrency(response.getString("currency"));
    //                 }
    //
    //             } catch (JSONException e) {
    //                 e.printStackTrace();
    //             }
    //         }
    //
    //     }, new Response.ErrorListener() {
    //         @Override
    //         public void onErrorResponse(VolleyError error) {
    //             error.printStackTrace();
    //         }
    //     });
    //
    //     Volley.newRequestQueue(ActivityProductList.this).add(request);
    // }

    // asynctask class to handle parsing json in background
    private class AsyncFetchProduct extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //this method will be running on UI thread
            progressDialog = new ProgressDialog(ActivityProductList.this);
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            FetchProduct();
            return null;
        }
    }

    void FetchProduct() {
        clearData();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                ProductAPI, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject product = data.getJSONObject(i);
                        ProductData productData = new ProductData();

                        if (product.has("id"))
                            productData.id = Long.parseLong(product.getString("id"));

                        if (product.has("name"))
                            productData.name = product.getString("name");

                        if (product.has("price"))
                            productData.price = Double.valueOf(formatData.format(product.getDouble("price")));

                        if (product.has("description"))
                            productData.desc = product.getString("description");

                        if (product.has("stock"))
                            productData.stock = Integer.parseInt(product.getString("stock"));

                        if (product.has("status"))
                            productData.status = Integer.parseInt(product.getString("status")) == 1 ? "Available" : "Not Available";

                        if (product.has("images")) {
                            JSONArray images = product.getJSONArray("images");
                            for (int j = 0; j < images.length(); j++) {
                                JSONObject imageData = images.getJSONObject(j);
                                Log.d(TAG, imageData.getString("name"));
                                productData.images.add(imageData.getString("name"));
                            }
                        }
                        Log.d(TAG, String.valueOf(productData.stock));
                        ProductData.add(productData);
                    }
                    if (AdapterProductList != null)
                        AdapterProductList.notifyDataSetChanged();
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityProductList.this, e.toString(), Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
            }
        });
        Log.d(TAG, "Product Request: " + request.toString());
        Volley.newRequestQueue(ActivityProductList.this).add(request);
    }

    // clear arraylist variables before used
    void clearData() {
        ProductData.clear();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        mla.imageLoader.clearCache();
        listProduct.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(final Configuration newConfig) {
        // Ignore orientation change to keep activity from restarting
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.open_main, R.anim.close_next);
    }
}
