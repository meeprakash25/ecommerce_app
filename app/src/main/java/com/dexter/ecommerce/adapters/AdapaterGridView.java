package com.dexter.ecommerce.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexter.ecommerce.GridViewItem;
import com.dexter.ecommerce.R;

public class AdapaterGridView extends ArrayAdapter<GridViewItem> {
	private Context mContext;
	private int resourceId;
	private ArrayList<GridViewItem> data;

	public AdapaterGridView(Context context, int layoutResourceId, ArrayList<GridViewItem> data) {
		super(context, layoutResourceId, data);
		this.mContext = context;
		this.resourceId = layoutResourceId;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View itemView = convertView;
		ViewHolder holder = null;

		if (itemView == null) {
			final LayoutInflater layoutInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			itemView = layoutInflater.inflate(resourceId, parent, false);

			holder = new ViewHolder();
			holder.imgItem = itemView.findViewById(R.id.thumbnails);
			holder.txtItem = itemView.findViewById(R.id.name);
			itemView.setTag(holder);
		} else {
			holder = (ViewHolder) itemView.getTag();
		}

		GridViewItem item = getItem(position);
		holder.imgItem.setImageDrawable(item.getImage());
		holder.txtItem.setText(item.getTitle());

		return itemView;
	}

	static class ViewHolder {
		ImageView imgItem;
		TextView txtItem;
	}
}