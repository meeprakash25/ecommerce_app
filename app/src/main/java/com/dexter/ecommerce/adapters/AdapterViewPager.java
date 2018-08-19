package com.dexter.ecommerce.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import static com.dexter.ecommerce.Constant.ProductImageDir;

public class AdapterViewPager extends PagerAdapter {
    Activity activity;
    String[] images;

    LayoutInflater inflater;

    public AdapterViewPager(Activity activity, String[] images){
        this.activity = activity;
        this.images = images;

    }

    @Override
    public int getCount() {
        return images.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View itemView = inflater.inflate(R.layout.viewpager_item,container,false);

        ImageView image;
//        image = itemView.findViewById(R.id.imageView);
        DisplayMetrics dis = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dis);
        int height = dis.heightPixels;
        int width = dis.widthPixels;
//        image.setMinimumHeight(height);
//        image.setMinimumWidth(width);

        try {
//            GlideApp.with(activity.getApplicationContext())
//                    .load(images[position])
//                    .diskCacheStrategy(DiskCacheStrategy.NONE)
//                    .into(image);
        }catch (Exception e){

        }

//        container.addView(itemView);
//        return itemView;
        return null;

    }
}
