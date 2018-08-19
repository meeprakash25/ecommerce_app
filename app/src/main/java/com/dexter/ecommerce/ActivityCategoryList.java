package com.dexter.ecommerce;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dexter.ecommerce.adapters.AdapterCategoryList;
import com.dexter.ecommerce.models.CategoryData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ActivityCategoryList extends Activity {

    private final static String LOGTAG = "CategoryList";

    public ProgressDialog progressDialog;

    public String CategoryAPI = Constant.CategoryAPI;

    RecyclerView listCategory;
    RecyclerView.Adapter AdapterCategoryList;
    List<com.dexter.ecommerce.models.CategoryData> CategoryData = new ArrayList<>();

    RecyclerView.LayoutManager recyclerViewLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.category_list);

        ActionBar bar = getActionBar();
        assert bar != null;
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);
        bar.setTitle("Category");

        CategoryAPI += "?accesskey=" + Constant.AccessKey;
        new AsyncFetchCategory().execute();
        listCategory = findViewById(R.id.listCategory);
        recyclerViewLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        listCategory.setLayoutManager(recyclerViewLayoutManager);
        AdapterCategoryList = new AdapterCategoryList(ActivityCategoryList.this, CategoryData);
        listCategory.setAdapter(AdapterCategoryList);
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
                Intent iMyOrder = new Intent(ActivityCategoryList.this, ActivityCart.class);
                startActivity(iMyOrder);
                overridePendingTransition(R.anim.open_next, R.anim.close_next);
                return true;

            case R.id.refresh:
                listCategory.invalidate();
                clearData();
                new AsyncFetchCategory().execute();
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

    // clear arraylist variables before used
    void clearData() {
        CategoryData.clear();
    }

    private class AsyncFetchCategory extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityCategoryList.this);
            progressDialog.setMessage("\tLoading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            FetchData();
            return null;
        }
    }

    protected void FetchData() {

        clearData();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                CategoryAPI, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                // Log.i(LOGTAG, response.toString());
                try {
                    JSONArray data = response.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject category = data.getJSONObject(i);
                        CategoryData categoryData = new CategoryData();
                        if (category.has("id"))
                            categoryData.id = category.getString("id");
                        if (category.has("image"))
                            categoryData.image = category.getString("image");
                        if (category.has("name"))
                            categoryData.name = category.getString("name");

                        CategoryData.add(categoryData);
                    }

                    if (AdapterCategoryList != null)
                        AdapterCategoryList.notifyDataSetChanged();
                    progressDialog.dismiss();

                } catch (JSONException e) {
                    progressDialog.dismiss();
                    Toast.makeText(ActivityCategoryList.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
                error.printStackTrace();
            }
        });
        Log.i(LOGTAG, request.toString());
        Volley.newRequestQueue(ActivityCategoryList.this).add(request);
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
//        cla.imageLoader.clearCache();
        listCategory.setAdapter(null);
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
