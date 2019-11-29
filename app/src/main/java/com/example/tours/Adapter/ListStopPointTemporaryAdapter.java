package com.example.tours.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tours.CreateStopPointActivity;
import com.example.tours.Model.StopPoint;
import com.example.tours.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListStopPointTemporaryAdapter extends ArrayAdapter<StopPoint> {
    private Context context;
    private Integer resource;
    private List<StopPoint> list;
    private List<String> listSerVice;
    private boolean canNotEdit;

    public ListStopPointTemporaryAdapter(@NonNull Context context, int resource, @NonNull List<StopPoint> objects, boolean canNotEdit) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.canNotEdit=canNotEdit;

        listSerVice = Arrays.asList("Restaurant", "Hotel", "Rest Station", "Other");
    }

    private static class ViewHolder {

        private final TextView tvStopPointName;
        private final TextView tvStopPointMinCost;
        private final TextView tvStopPointMaxCost;
        private final TextView tvStopPointArriveAt;
        private final TextView tvStopPointLeaveAt;
        private final TextView tvStopPointService;
        private final ImageView btnDeleteStopPoint;
        private final ImageView btnGeoLocateStopPoint;
        private final ImageView btnEditStopPoint;

        //code

        private ViewHolder(View row) {
            tvStopPointName = row.findViewById(R.id.tv_stop_point_name);
            tvStopPointMinCost=row.findViewById(R.id.tv_stop_point_min_cost);
            tvStopPointMaxCost=row.findViewById(R.id.tv_stop_point_max_cost);
            tvStopPointArriveAt=row.findViewById(R.id.tv_stop_point_startDate);
            tvStopPointLeaveAt=row.findViewById(R.id.tv_stop_point_endDate);
            tvStopPointService=row.findViewById(R.id.tv_stop_point_service);
            btnDeleteStopPoint=row.findViewById(R.id.map_delete_stop_point);
            btnGeoLocateStopPoint=row.findViewById(R.id.map_btn_view_stop_point);
            btnEditStopPoint=row.findViewById(R.id.map_btn_edit_stop_point);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View row;
        ListStopPointTemporaryAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListStopPointTemporaryAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListStopPointTemporaryAdapter.ViewHolder) row.getTag();
        }

        StopPoint stopPoint = list.get(position);
        holder.tvStopPointName.setText(stopPoint.getName());
        holder.tvStopPointService.setText(listSerVice.get(stopPoint.getServiceTypeId()-1));
        holder.tvStopPointMinCost.setText(stopPoint.getMinCost()+ " VND");
        holder.tvStopPointMaxCost.setText(stopPoint.getMaxCost() +" VND");
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(stopPoint.getArrivalAt());
        String date1 = DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
        holder.tvStopPointArriveAt.setText(date1);
        cal.setTimeInMillis(stopPoint.getLeaveAt());
        String date2 = DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
        holder.tvStopPointLeaveAt.setText(date2);

        if(canNotEdit){
            holder.btnDeleteStopPoint.setVisibility(View.GONE);
        }
        else {
            holder.btnDeleteStopPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showConfirmBox(position);
                }
            });
        }

        holder.btnGeoLocateStopPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CreateStopPointActivity)context).moveCameraWhenSelectStopPoint(stopPoint);
            }
        });
        if(canNotEdit){
            holder.btnEditStopPoint.setVisibility(View.GONE);
        }
        else {
            holder.btnEditStopPoint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((CreateStopPointActivity) context).showEditStopPointDialog(position, stopPoint, date1, date2);
                }
            });
        }

        return row;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public StopPoint getItem(int position) {
        return list.get(position);
    }

    private void showConfirmBox(int position){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Xóa");
        alert.setMessage("Xóa điểm dừng này?");
        alert.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                StopPoint stopPoint=list.get(position);

                ((CreateStopPointActivity)context).removeStopPointMarker(stopPoint);

                if(stopPoint.getId()!=null){
                    ((CreateStopPointActivity)context).addToDeleteList(stopPoint.getId());
                }
                else{
                    ((CreateStopPointActivity)context).removeTemporaryStopPoint(stopPoint);
                }
                list.remove(position);
                notifyDataSetChanged();
                dialog.dismiss();
            }
        });

        alert.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alert.show();
    }
}
