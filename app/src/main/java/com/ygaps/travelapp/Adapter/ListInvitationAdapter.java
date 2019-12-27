package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.DialogProgressBar;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.TourInvitation;
import com.ygaps.travelapp.R;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListInvitationAdapter extends ArrayAdapter<TourInvitation> {
    private Context context;
    private Integer resource;
    private List<TourInvitation> list;
    private APITour apiTour;

    public static String EDIT_ID_TOUR = "EDIT ID TOUR";

    public ListInvitationAdapter(@NonNull Context context, int resource, @NonNull List<TourInvitation> objects, APITour apiTour) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.apiTour= apiTour;
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
        private final TextView tvStatus;

        private final ImageView hostAvatar;
        private final TextView hostName;
        private final TextView hostPhone;
        private final TextView tvCreated;

        private Button btnAccept;
        private Button btnRefuse;


        private ViewHolder(View row) {
            imgAvater = row.findViewById(R.id.img_avatar);
            tvTourName = row.findViewById(R.id.tv_tourName);
            tvStartDate = row.findViewById(R.id.tv_startDate);
            tvEndDate = row.findViewById(R.id.tv_endDate);
            tvAdults = row.findViewById(R.id.tv_adults);
            tvChilds = row.findViewById(R.id.tv_childs);
            tvMinCost = row.findViewById(R.id.tv_minCost);
            tvMaxCost = row.findViewById(R.id.tv_maxCost);
            tvStatus = row.findViewById(R.id.tour_status);

            hostAvatar = row.findViewById(R.id.host_avatar);
            hostName = row.findViewById(R.id.tv_host_name);
            hostPhone =row.findViewById(R.id.tv_host_phone);
            tvCreated = row.findViewById(R.id.created);

            btnAccept = row.findViewById(R.id.btn_accept_invitation);
            btnRefuse = row.findViewById(R.id.btn_refuse_invitation);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ListInvitationAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListInvitationAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListInvitationAdapter.ViewHolder) row.getTag();
        }

        TourInvitation tour = list.get(position);

        // set thuoc tinh cho item:
        if (tour.getAvatar() != null){
            Picasso.get().load(tour.getAvatar()).into(holder.imgAvater);
        }else{
            holder.imgAvater.setImageResource(R.drawable.empty_image);
        }

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

        if(tour.getStatus()==-1){
            holder.tvStatus.setText(R.string.tour_cancled);
            holder.tvStatus.setTextColor(context.getResources().getColor(R.color.colorCustomPrimary));
        }
        else if(tour.getStatus()==0){
            holder.tvStatus.setText(R.string.tour_open);
        }
        else if(tour.getStatus()==1){
            holder.tvStatus.setText(R.string.tour_started);
        }
        else if(tour.getStatus()==2){
            holder.tvStatus.setText(R.string.tour_closed);
        }

        if (tour.getHostAvatar() != null){
            Picasso.get().load(tour.getHostAvatar()).into(holder.hostAvatar);
        }
        else{
            holder.hostAvatar.setImageResource(R.drawable.unknown_user);
        }

        if(tour.getCreatedOn()!=null){
            cal.setTimeInMillis(Long.parseLong(tour.getCreatedOn()));
            String date1 = android.text.format.DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
            holder.tvCreated.setText(date1);
        }

        if(tour.getHostName()==null){
            if(tour.getHostPhone()!=null){
                holder.hostName.setText(tour.getHostPhone());
                if(tour.getHostEmail()!=null){
                    holder.hostPhone.setText(tour.getHostEmail());
                }
            }
            else if(tour.getHostEmail()!=null){
                holder.hostName.setText(tour.getHostEmail());
            }
        }
        else{
            holder.hostName.setText(tour.getHostName());
            if(tour.getHostPhone()!=null){
                holder.hostPhone.setText(tour.getHostPhone());
            }
            else if(tour.getHostEmail()!=null){
                holder.hostPhone.setText(tour.getHostEmail());
            }
        }

        holder.btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProgressBar.showProgress(context);
                apiTour.responseInvitation(TokenStorage.getInstance().getAccessToken(),tour.getId(),true).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if(response.isSuccessful()){
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context,R.string.accept_successfully, Toast.LENGTH_SHORT).show();
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(context,R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
                    }
                });
            }
        });

        holder.btnRefuse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogProgressBar.showProgress(context);
                apiTour.responseInvitation(TokenStorage.getInstance().getAccessToken(),tour.getId(),false).enqueue(new Callback<MessageResponse>() {
                    @Override
                    public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                        if(response.isSuccessful()){
                            list.remove(position);
                            notifyDataSetChanged();
                            Toast.makeText(context,R.string.refuse_successfully, Toast.LENGTH_SHORT).show();
                        }
                        DialogProgressBar.closeProgress();
                    }

                    @Override
                    public void onFailure(Call<MessageResponse> call, Throwable t) {
                        Toast.makeText(context,R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                        DialogProgressBar.closeProgress();
                    }
                });
            }
        });

        return row;

    }

}
