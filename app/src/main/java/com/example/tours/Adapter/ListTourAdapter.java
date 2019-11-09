package com.example.tours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tours.Model.Tour;
import com.example.tours.R;

import java.util.List;

public class ListTourAdapter extends ArrayAdapter<Tour> {
    private Context context;
    private Integer resource;
    private List<Tour> list;

    public ListTourAdapter(@NonNull Context context, int resource, @NonNull List<Tour> objects) {
        super(context, resource, objects);
        this.context=context;
        this.resource=resource;
        this.list=objects;

    }
    private static class ViewHolder {

        private final TextView tourName;
        //code

        private ViewHolder(View row) {
            tourName = row.findViewById(R.id.tourName);
            //code
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        ViewHolder holder;

        if(convertView==null) {
            LayoutInflater inflater= (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        }
        else{
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        Tour tour=list.get(position);

        //code
        holder.tourName.setText(tour.getName());

        return  row;

    }
}
