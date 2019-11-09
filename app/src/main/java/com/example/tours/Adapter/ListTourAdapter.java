package com.example.tours.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tours.Model.Tour;
import com.example.tours.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ListTourAdapter extends ArrayAdapter<Tour> {
    private Context context;
    private Integer resource;
    private List<Tour> list;

    public ListTourAdapter(@NonNull Context context, int resource, @NonNull List<Tour> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;

    }

    private static class ViewHolder {

        private final ImageView imgAvater;
        private final TextView tvTourName;
        private final TextView tvStartDate;
        private final TextView tvEndDate;
        private final TextView tvAdults;
        private final TextView tvChilds;
        private final TextView tvMinCost;
        private final TextView tvMaxCost;
        //code

        private ViewHolder(View row) {
            imgAvater = (ImageView) row.findViewById(R.id.img_avatar);
            tvTourName = (TextView) row.findViewById(R.id.tv_tourName);
            tvStartDate = (TextView) row.findViewById(R.id.tv_startDate);
            tvEndDate = (TextView) row.findViewById(R.id.tv_endDate);
            tvAdults = (TextView) row.findViewById(R.id.tv_adults);
            tvChilds = (TextView) row.findViewById(R.id.tv_childs);
            tvMinCost = (TextView) row.findViewById(R.id.tv_minCost);
            tvMaxCost = (TextView) row.findViewById(R.id.tv_maxCost);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View row;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ViewHolder) row.getTag();
        }

        Tour tour = list.get(position);

        // set thuoc tinh cho item:
        if (tour.getAvatar() != null){
            Picasso.get().load(tour.getAvatar()).into(holder.imgAvater);
        }

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(holder.imgAvater);
        holder.tvTourName.setText(tour.getName());
        holder.tvStartDate.setText(tour.getStartDate());
        holder.tvEndDate.setText(tour.getEndDate());
        holder.tvAdults.setText(tour.getAdults().toString() + " adults ");
        holder.tvChilds.setText(tour.getChilds().toString() + " childs");
        holder.tvMinCost.setText(tour.getMinCost());
        holder.tvMaxCost.setText(tour.getMaxCost());

        return row;

    }


}

