package com.squeezymo.boloid.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.squeezymo.boloid.R;
import com.squeezymo.boloid.model.TaskItem;
import java.util.List;

public class PriceListAdapter extends ArrayAdapter<TaskItem.Price> {

    public PriceListAdapter(Context context, List<TaskItem.Price> prices) {
        super(context, 0, prices);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemLayout = (convertView == null ? LayoutInflater.from(getContext()).inflate(R.layout.viewholder_price, parent, false) : convertView);

        TextView priceView = (TextView) itemLayout.findViewById(R.id.price);
        TextView descriptionView = (TextView) itemLayout.findViewById(R.id.description);

        TaskItem.Price price = getItem(position);
        priceView.setText(Integer.toString(price.getPrice()));
        descriptionView.setText(price.getDescription());

        return itemLayout;
    }

}