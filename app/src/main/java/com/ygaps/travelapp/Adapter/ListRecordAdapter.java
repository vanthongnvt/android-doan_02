package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ygaps.travelapp.Model.Record;
import com.ygaps.travelapp.R;

import java.util.List;

public class ListRecordAdapter extends ArrayAdapter<Record> {
    private Context context;
    private List<Record> records;
    private int resource;
    public ListRecordAdapter(@NonNull Context context, int resource, @NonNull List<Record> objects) {
        super(context, resource, objects);
        this.context=context;
        this.records = objects;
        this.resource =resource;
    }

    private static class ViewHolder {

//        private final TextView tvRecord;

        private ViewHolder(View row) {
//            tvRecord = row.findViewById(R.id.tv_stop_point_name);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ListRecordAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListRecordAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListRecordAdapter.ViewHolder) row.getTag();
        }

//        Record record = records.get(position);

        return row;
    }
}
