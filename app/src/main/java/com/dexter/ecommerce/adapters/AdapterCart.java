package com.dexter.ecommerce.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.dexter.ecommerce.R;
import com.dexter.ecommerce.models.OrderData;
import com.dexter.ecommerce.models.SettingData;

import java.util.List;

// adapter class for custom order list
public class AdapterCart extends BaseAdapter {
    private LayoutInflater inflater;
    private List<OrderData> data;

    public AdapterCart(Context context, List<OrderData> orderData) {
        inflater = LayoutInflater.from(context);
        this.data = orderData;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.order_list_item, null);
            holder = new ViewHolder();
            holder.txtProductName = convertView.findViewById(R.id.txtProductName);
            holder.txtQuantity = convertView.findViewById(R.id.txtQuantity);
            holder.txtPrice = convertView.findViewById(R.id.txtPrice);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtProductName.setText(data.get(position).getProductName());
        holder.txtQuantity.setText(String.valueOf(data.get(position).getQuantity()));
        holder.txtPrice.setText(data.get(position).getPrice() + " " + SettingData.getCurrency());
        return convertView;
    }

    static class ViewHolder {
        TextView txtProductName, txtQuantity, txtPrice;
    }
}