package com.dexter.ecommerce;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Constant {
	
	// API URL configuration
	public static String AdminPageURL = "http://ecommerce.test";
	public static String CategoryAPI = "http://ecommerce.test/api/category";
	public static String ProductAPI = "http://ecommerce.test/api/product-by-category";
	public static String TaxCurrencyAPI = "http://ecommerce.test/api/setting";
	public static String ProductDetailAPI = "http://ecommerce.test/api/product";
	public static String SendDataAPI = "http://ecommerce.test/api/order";

	public static String ProductImageDir = "http://ecommerce.test/images/product";
	public static String CategoryImageDir = "http://ecommerce.test/images/category";


	
	// change this access similar with accesskey in admin panel for security reason
	public static String AccessKey = "12345";
	
	// database path configuration
	public static String DBPath = "/data/data/com.dexter.ecommerce/databases";
	
	// method to check internet connection
	public static boolean isNetworkAvailable(Activity activity) {
		ConnectivityManager connectivity = (ConnectivityManager) activity
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// method to handle images from server
	public static void CopyStream(InputStream is, OutputStream os)
    {
        final int buffer_size=1024;
        try
        {
            byte[] bytes=new byte[buffer_size];
            for(;;)
            {
              int count=is.read(bytes, 0, buffer_size);
              if(count==-1)
                  break;
              os.write(bytes, 0, count);
            }
        }
        catch(Exception ex){}
    }

}
