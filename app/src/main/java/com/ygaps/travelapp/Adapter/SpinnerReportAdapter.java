package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ygaps.travelapp.Model.SpinnerReportItem;
import com.ygaps.travelapp.R;

import java.util.List;

public class SpinnerReportAdapter extends ArrayAdapter<SpinnerReportItem> {
    private Context context;
    private Integer resource;
    private List<SpinnerReportItem> list;

    public SpinnerReportAdapter(@NonNull Context context, int resource, @NonNull List<SpinnerReportItem> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_report_row, parent, false
            );
        }

        SpinnerReportItem item = list.get(position);
        TextView text = convertView.findViewById(R.id.reportspinner_item_text);
        ImageView img = convertView.findViewById(R.id.reportspinner_item_image);
        ImageView menu = convertView.findViewById(R.id.reportspinner_item_menu);

        text.setText(item.getText());
        img.setImageResource(item.getImg());
        menu.setVisibility(View.GONE);

        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
                    R.layout.spinner_report_row, parent, false
            );
        }

        SpinnerReportItem item = list.get(position);
        TextView text = convertView.findViewById(R.id.reportspinner_item_text);
        ImageView img = convertView.findViewById(R.id.reportspinner_item_image);
        ImageView menu = convertView.findViewById(R.id.reportspinner_item_menu);

        text.setText(item.getText());
        img.setImageResource(item.getImg());
        menu.setVisibility(View.GONE);

        return convertView;
    }
}
