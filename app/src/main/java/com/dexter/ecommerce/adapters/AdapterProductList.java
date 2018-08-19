package com.dexter.ecommerce.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dexter.ecommerce.ActivityProductDetail;
import com.dexter.ecommerce.ActivityProductList;
import com.dexter.ecommerce.GlideApp;
import com.dexter.ecommerce.R;
import com.dexter.ecommerce.models.ProductData;
import com.dexter.ecommerce.models.SettingData;

import java.util.List;

import static com.dexter.ecommerce.Constant.ProductImageDir;

public class AdapterProductList extends RecyclerView.Adapter<AdapterProductList.MyHolder> {
    public static final String TAG = "ProductAdapter";
    List<ProductData> data;
    private Context context;

    public AdapterProductList(Activity context, List<ProductData> data) {
        this.data = data;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.product_list_item, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder myHolder, final int position) {
        final ProductData current = data.get(position);
        myHolder.textName.setText(current.name);
        myHolder.textPrice.setText(String.valueOf(current.price) + " " + SettingData.getCurrency());
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            myHolder.textDesc.setText(Html.fromHtml(current.desc));
        } else {
            myHolder.textDesc.setText(Html.fromHtml(current.desc, Html.FROM_HTML_MODE_COMPACT));
        }

        for (int i = 0; i < 1; i++) {
            GlideApp.with(context)
                    .load(ProductImageDir + "/" + current.images.get(i))
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(myHolder.imageView);
        }


        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                Intent intent = new Intent(context, ActivityProductDetail.class);
                intent.putExtra("productId", current.id);
                intent.putExtra("productName", current.name);
                intent.putExtra("productPrice", current.price);
                intent.putExtra("productStatus", current.status);
                intent.putExtra("productDesc", current.desc);
                intent.putExtra("productStock", current.stock);
                intent.putExtra("productImages", current.images);

                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.open_next, R.anim.close_next);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }


    public static class MyHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textName;
        public TextView textPrice;
        public TextView textDesc;
        public CardView cardView;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgImageView);
            textName = itemView.findViewById(R.id.txtName);
            textPrice = itemView.findViewById(R.id.txtPrice);
            textDesc = itemView.findViewById(R.id.textDesc);
            cardView = itemView.findViewById(R.id.productCard);
        }
    }
}
