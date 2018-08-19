package com.dexter.ecommerce;

import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dexter.ecommerce.databases.OrdersDataSource;
import com.dexter.ecommerce.models.OrderData;
import com.dexter.ecommerce.models.SettingData;

import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ActivityCheckout extends FragmentActivity {

    private final static String LOGTAG = "activitycheckout";

    String SendDataAPI = Constant.SendDataAPI;

    ProgressDialog progressDialog;

    Button btnSend;
    static Button btnDate;
    static Button btnTime;
    EditText edtName, edtShipping, edtPhone, edtOrderList, edtComment, edtAddress, edtEmail, edtCity, edtProvince;
    ScrollView sclDetail;

    OrdersDataSource dataSource;
    public static List<OrderData> data;

    // declare string variables to store data
    String name, shipping_type, date, time, phone, date_time, address, email, city, province;
    String orderList = "";
    String comment = "";

    // declare static variables to store tax and currency data
    static Double tax = SettingData.getTax();
    static String currency = SettingData.getCurrency();

    static final String TIME_DIALOG_ID = "timePicker";
    static final String DATE_DIALOG_ID = "datePicker";

    // addMyOrder price format
    DecimalFormat formatData = new DecimalFormat("#.##");

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkout);

        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.header)));
        bar.setTitle("Checkout");
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setHomeButtonEnabled(true);

        SendDataAPI += "?accesskey=" + Constant.AccessKey;

        edtName = findViewById(R.id.edtName);
        edtShipping = findViewById(R.id.edtShipping);
        edtEmail = findViewById(R.id.edtEmail);
        btnDate = findViewById(R.id.btnDate);
        btnTime = findViewById(R.id.btnTime);
        edtPhone = findViewById(R.id.edtPhone);
        edtOrderList = findViewById(R.id.edtOrderList);
        edtComment = findViewById(R.id.edtComment);
        btnSend = findViewById(R.id.btnSend);
        sclDetail = findViewById(R.id.sclDetail);
        edtAddress = findViewById(R.id.edtAddress);
        edtCity = findViewById(R.id.edtKota);
        edtProvince = findViewById(R.id.edtProvince);
        Spinner spinner = findViewById(R.id.spinner1);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.shipping_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {
                // TODO Auto-generated method stub

                switch (arg2) {

                    case 0:
                        edtShipping.setText(R.string.POS); //POS
                        break;
                    case 1:
                        edtShipping.setText(R.string.JNE); //JNE
                        break;
                    case 2:
                        edtShipping.setText(R.string.TIKI); //TIKI
                        break;
                    default:
                        edtShipping.setText(R.string.COD); //COD
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });

        // event listener to handle date button when pressed
        btnDate.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                // show date picker dialog
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getSupportFragmentManager(), DATE_DIALOG_ID);
            }
        });

        // event listener to handle time button when pressed
        btnTime.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                // show time picker dialog
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getSupportFragmentManager(), TIME_DIALOG_ID);
            }
        });

        // get orders from database
        getDataFromDatabase();

        // event listener to handle send button when pressed
        btnSend.setOnClickListener(new OnClickListener() {

            public void onClick(View arg0) {
                // TODO Auto-generated method stub

                // get data from all forms and send to server
                name = edtName.getText().toString();
                address = edtAddress.getText().toString();
                city = edtCity.getText().toString();
                province = edtProvince.getText().toString();
                email = edtEmail.getText().toString();
                shipping_type = edtShipping.getText().toString();
                date = btnDate.getText().toString();
                time = btnTime.getText().toString();
                phone = edtPhone.getText().toString();
                comment = edtComment.getText().toString();
                date_time = date + time;
                if (name.equalsIgnoreCase("") || shipping_type.equalsIgnoreCase("") || email.equalsIgnoreCase("") || address.equalsIgnoreCase("") || city.equalsIgnoreCase("") || province.equalsIgnoreCase("") ||
                        date.equalsIgnoreCase(getString(R.string.date)) ||
                        time.equalsIgnoreCase(getString(R.string.time)) ||
                        phone.equalsIgnoreCase("")) {
                    Toast.makeText(ActivityCheckout.this, R.string.form_alert, Toast.LENGTH_LONG).show();

                } else if (!validateEmail(email)) {
                    Toast.makeText(ActivityCheckout.this, "Please enter a valid email", Toast.LENGTH_LONG).show();
                } else if ((data.size() == 0)) {
                    Toast.makeText(ActivityCheckout.this, R.string.order_alert, Toast.LENGTH_LONG).show();
                } else {

                    new SendData().execute();
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

    // method to addMyOrder date picker dialog
    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // set default date
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // show selected date to date button
            btnDate.setText(new StringBuilder()
                    .append(year).append("-")
                    .append(month + 1).append("-")
                    .append(day).append(" "));
        }
    }

    // method to addMyOrder time picker dialog
    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // set default time
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of DatePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            // show selected time to time button
            btnTime.setText(new StringBuilder()
                    .append(pad(hourOfDay)).append(":")
                    .append(pad(minute)).append(":")
                    .append("00"));
        }
    }

    public static boolean validateEmail(String emailStr) {
        Matcher matcher = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE).matcher(emailStr);
        return matcher.find();
    }

    private class SendData extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(ActivityCheckout.this);
            progressDialog.setMessage("\tSending data. Please wait...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {

            // try {
            //     Thread.sleep(5000);
            // } catch (InterruptedException e) {
            //     e.printStackTrace();
            // }
            POSTData();
            return null;
        }
    }

    public void POSTData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, SendDataAPI,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        resultAlert("OK", response);
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                resultAlert("Failed", error.toString());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("Content/Type", "application/json");
                params.put("name", name);
                params.put("address", address);
                params.put("city", city);
                params.put("province", province);
                params.put("shipping_type", shipping_type);
                params.put("date_time", date_time);
                params.put("phone", phone);
                params.put("order_list", orderList);
                params.put("comment", comment);
                params.put("email", email);
                params.put("status", "0");
                Log.i(LOGTAG, String.valueOf(params));
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(ActivityCheckout.this).add(stringRequest);
    }

    // method to show toast message
    public void resultAlert(String message, String response) {
        if (message.trim().equalsIgnoreCase("OK")) {
            progressDialog.dismiss();
            Toast.makeText(ActivityCheckout.this, response, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(ActivityCheckout.this, ActivityConfirmMessage.class);
            startActivity(intent);
            overridePendingTransition(R.anim.open_next, R.anim.close_next);
            finish();
        } else if (message.trim().equalsIgnoreCase("Failed")) {
            progressDialog.dismiss();
            Toast.makeText(ActivityCheckout.this, R.string.failed_alert, Toast.LENGTH_SHORT).show();
        }
    }

    // method to get data from database
    public void getDataFromDatabase() {
        dataSource = new OrdersDataSource(this);
        dataSource.open();
        data = dataSource.findAll();

        double totalPrice = 0;
        double subTotalPrice = 0;
        double subTotalPriceWithTax = 0;
        double totalTax = 0;

        // store all data to variables
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < data.size(); i++) {
            totalPrice = data.get(i).getPrice();
            subTotalPrice += data.get(i).getPrice();
            stringBuilder.append("Product name: ").append(data.get(i).getProductName()).append("\nNumber of Items: ").append(data.get(i).getQuantity()).append("\nPrice(per item): ").append(data.get(i).getPrice() / data.get(i).getQuantity()).append(" ").append(currency).append("\nTotal Price: ").append(totalPrice).append(" ").append(currency).append(",\n");
        }
        orderList = stringBuilder.toString() + "\nSub Total: " + subTotalPrice + " " + currency;

        totalTax = Double.parseDouble(formatData.format(subTotalPrice * (tax / 100)));
        subTotalPriceWithTax = Double.parseDouble(formatData.format(subTotalPrice + totalTax));
        orderList += "\nSub Total(with " + tax + "% VAT): " + subTotalPriceWithTax + " " + currency;

        edtOrderList.setText(orderList);
    }

    // method to format date
    private static String pad(int c) {
        if (c >= 10) {
            return String.valueOf(c);
        } else {
            return "0" + String.valueOf(c);
        }
    }

    // when back button pressed close database and back to previous page
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        dataSource.close();
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
