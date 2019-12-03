package com.example.tours.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tours.Model.Tour;
import com.example.tours.Model.UserTour;
import com.example.tours.R;
import com.example.tours.UpdateTourActivity;
import com.squareup.picasso.Picasso;

import java.nio.BufferUnderflowException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UserListTourAdapter extends ArrayAdapter<UserTour> {
    private Context context;
    private Integer resource;
    private List<UserTour> list;
    private List<UserTour> list_backup;

    public UserListTourAdapter(@NonNull Context context, int resource, @NonNull List<UserTour> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.list_backup = new ArrayList<>();
        this.list_backup.addAll(objects);
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
        private final ImageView btnEdit;


        private ViewHolder(View row) {
            imgAvater = (ImageView) row.findViewById(R.id.listusertrip_item_img_avatar);
            tvTourName = (TextView) row.findViewById(R.id.listusertrip_item_tv_tourName);
            tvStartDate = (TextView) row.findViewById(R.id.listusertrip_item_tv_startDate);
            tvEndDate = (TextView) row.findViewById(R.id.listusertrip_item_tv_endDate);
            tvAdults = (TextView) row.findViewById(R.id.listusertrip_item_tv_adults);
            tvChilds = (TextView) row.findViewById(R.id.listusertrip_item_tv_childs);
            tvMinCost = (TextView) row.findViewById(R.id.listusertrip_item_tv_minCost);
            tvMaxCost = (TextView) row.findViewById(R.id.listusertrip_item_tv_maxCost);
            btnEdit = (ImageView) row.findViewById(R.id.btn_edit_user_tour);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        UserListTourAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new UserListTourAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (UserListTourAdapter.ViewHolder) row.getTag();
        }

        UserTour tour = list.get(position);

        // set thuoc tinh cho item:
        if (tour.getAvatar() != null){
            Picasso.get().load(tour.getAvatar()).into(holder.imgAvater);
        }else{
            holder.imgAvater.setImageResource(R.drawable.empty_image);
        }

        //Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(holder.imgAvater);
        //Picasso.get().load("https://dulich-api.herokuapp.com/images/tours/1553545313571.jpg").into(holder.imgAvater);
        holder.tvTourName.setText(tour.getName());

        Calendar cal = Calendar.getInstance();
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(tour.getStartDate() == null){
            holder.tvStartDate.setText("00/00/0000");
        }
        else{
            time = Long.parseLong(tour.getStartDate());
            cal.setTimeInMillis(time);
            Date timeStartDate = cal.getTime();
            holder.tvStartDate.setText(dateFormat.format(timeStartDate));
        }

        if(tour.getEndDate() == null){
            holder.tvEndDate.setText("00/00/0000");
        }
        else{
            time = Long.parseLong(tour.getEndDate());
            cal.setTimeInMillis(time);
            Date timeEndDate = cal.getTime();
            holder.tvEndDate.setText(dateFormat.format(timeEndDate));
        }
        if(tour.getAdults() == null){
            holder.tvAdults.setText("0 người lớn ");
        }
        else{
            holder.tvAdults.setText(tour.getAdults().toString() + " người lớn ");
        }
        if(tour.getChilds() == null){
            holder.tvChilds.setText("0 trẻ em");
        }
        else{
            holder.tvChilds.setText(tour.getChilds().toString() + " trẻ em");
        }
        if(tour.getMinCost() == null){
            holder.tvMinCost.setText("0");
        }
        else{
            holder.tvMinCost.setText(tour.getMinCost());
        }
        if(tour.getMaxCost() == null){
            holder.tvMaxCost.setText("0");
        }
        else{
            holder.tvMaxCost.setText(tour.getMaxCost());
        }

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UpdateTourActivity.class);
                intent.putExtra("UserTour", tour);
                context.startActivity(intent);
            }
        });

        return row;

    }

    public int  filter(String s) {
        list.clear();
        if(s == ""){
            list.addAll(list_backup);
        }
        else {
            for(UserTour item: list_backup){
                if(item.getName() != null && item.getName().contains(s)){
                    list.add(item);
                }
            }
        }
        notifyDataSetChanged();
        return list.size();
    }

}
