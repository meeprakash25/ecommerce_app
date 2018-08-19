package com.dexter.ecommerce.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.dexter.ecommerce.ActivityProductList;
import com.dexter.ecommerce.GlideApp;
import com.dexter.ecommerce.R;
import com.dexter.ecommerce.models.CategoryData;

import java.util.List;

import static com.dexter.ecommerce.Constant.CategoryImageDir;

public class AdapterCategoryList extends RecyclerView.Adapter<AdapterCategoryList.MyHolder> {
    public static final String TAG = "CategoryAdapter";
    private List<CategoryData> data;
    private Context context;

    public AdapterCategoryList(Activity context, List<CategoryData> data) {
        this.data = data;
        this.context = context;
    }

    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.category_list_item, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(MyHolder myHolder, final int position) {
        final CategoryData current = data.get(position);
        myHolder.textName.setText(current.name);
        GlideApp.with(context)
                .load(CategoryImageDir + "/" + current.image)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(myHolder.imageView);

        myHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Activity activity = (Activity) context;
                Intent intent = new Intent(context, ActivityProductList.class);
                intent.putExtra("categoryId", current.id);
                intent.putExtra("categoryName", current.name);
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
        public CardView cardView;

        public MyHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imgThumb);
            textName = itemView.findViewById(R.id.txtText);
            cardView = itemView.findViewById(R.id.categoryCard);
        }
    }
}
