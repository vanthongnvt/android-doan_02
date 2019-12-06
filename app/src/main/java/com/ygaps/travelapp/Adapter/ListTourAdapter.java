package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.CloneTour;
import com.ygaps.travelapp.Model.Tour;
import com.ygaps.travelapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTourAdapter extends ArrayAdapter<Tour> {
    private Context context;
    private Integer resource;
    private List<Tour> list;
    private List<Tour> list_backup;
    private APITour apiTour;

    public ListTourAdapter(@NonNull Context context, int resource, @NonNull List<Tour> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.list_backup = new ArrayList<>();
        this.list_backup.addAll(objects);
        apiTour = new APIRetrofitCreator().getAPIService();
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
        private  final ImageButton imgbtnClone;


        private ViewHolder(View row) {
            imgAvater = (ImageView) row.findViewById(R.id.img_avatar);
            tvTourName = (TextView) row.findViewById(R.id.tv_tourName);
            tvStartDate = (TextView) row.findViewById(R.id.tv_startDate);
            tvEndDate = (TextView) row.findViewById(R.id.tv_endDate);
            tvAdults = (TextView) row.findViewById(R.id.tv_adults);
            tvChilds = (TextView) row.findViewById(R.id.tv_childs);
            tvMinCost = (TextView) row.findViewById(R.id.tv_minCost);
            tvMaxCost = (TextView) row.findViewById(R.id.tv_maxCost);
            imgbtnClone = (ImageButton) row.findViewById(R.id.listtouritem_btn_clone_tour);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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

        holder.imgbtnClone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProgressBar.showProgress(context);
                Number id = tour.getId();
                apiTour.cloneTour(TokenStorage.getInstance().getAccessToken(), id).enqueue(new Callback<CloneTour>() {
                    @Override
                    public void onResponse(Call<CloneTour> call, Response<CloneTour> response) {
                        if(response.isSuccessful()){
                            Toast.makeText(context, "Nhân bản tour thành công", Toast.LENGTH_SHORT).show();
                        }
                        else if(response.code() == 500){
                            Toast.makeText(context, "Lỗi server, không thể nhân bản tour này", Toast.LENGTH_SHORT).show();
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<CloneTour> call, Throwable t) {
                        Toast.makeText(context, R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
                    }
                });
            }
        });

        return row;

    }

    public int filter(String s) {
        list.clear();
        if(s == ""){
            list.addAll(list_backup);
        }
        else {
            for(Tour item: list_backup){
                if(item.getName() != null && item.getName().contains(s)){
                    list.add(item);
                }
            }
        }
        notifyDataSetChanged();
        return list.size();
    }

}

