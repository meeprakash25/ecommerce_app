package com.dexter.ecommerce;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.dexter.ecommerce.models.SettingData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ActivitySplash extends Activity {

    private final static String LOGTAG = "activitysplash";

    public String TaxCurrencyAPI = Constant.TaxCurrencyAPI;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        TaxCurrencyAPI += "?accesskey=" + Constant.AccessKey;
        new AsyncFetchTax().execute();

        /** Sets a layout for this activity */
        setContentView(R.layout.splash);

    }

    //    fetch Tax Currency
    private class AsyncFetchTax extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {

            JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,
                    TaxCurrencyAPI, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    SettingData settingData = new SettingData();
                    try {
                        if (response.has("tax")) {
                            settingData.setTax(Double.parseDouble(response.getString("tax")));
                        }

                        if (response.has("currency")) {
                            settingData.setCurrency(response.getString("currency"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                }
            });

            Volley.newRequestQueue(ActivitySplash.this).add(request);

            sleep(5000);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            /** Creates an intent to start new activity */
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
            // Do things like hide the progress bar or change a TextView
        }
    }

    private void sleep(Integer milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}