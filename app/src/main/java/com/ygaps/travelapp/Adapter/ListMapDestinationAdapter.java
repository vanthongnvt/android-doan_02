package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.ui.map.MapFragment;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListMapDestinationAdapter extends ArrayAdapter<StopPoint> {
    private Context context;
    private Integer resource;
    private List<StopPoint> list;
    private List<String> listSerVice;
    private int positionChecked=0;
    private Fragment fragment;

    public ListMapDestinationAdapter(@NonNull Context context, int resource, @NonNull List<StopPoint> objects, Fragment fragment) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.fragment =fragment;
        listSerVice = Arrays.asList("Restaurant", "Hotel", "Rest Station", "Other");
    }

    private static class ViewHolder {

        private final TextView tvStopPointName;
//        private final TextView tvStopPointMinCost;
//        private final TextView tvStopPointMaxCost;
//        private final TextView tvStopPointArriveAt;
//        private final TextView tvStopPointLeaveAt;
//        private final TextView tvStopPointService;

        private final Switch aSwitch;
        private final ImageView showDestinationInfo;

        //code

        private ViewHolder(View row) {
            tvStopPointName = row.findViewById(R.id.tv_stop_point_name);
//            tvStopPointMinCost = row.findViewById(R.id.tv_stop_point_min_cost);
//            tvStopPointMaxCost = row.findViewById(R.id.tv_stop_point_max_cost);
//            tvStopPointArriveAt = row.findViewById(R.id.tv_stop_point_startDate);
//            tvStopPointLeaveAt = row.findViewById(R.id.tv_stop_point_endDate);
//            tvStopPointService = row.findViewById(R.id.tv_stop_point_service);
            aSwitch = row.findViewById(R.id.switch_select_destination);
            showDestinationInfo = row.findViewById(R.id.show_destination_info);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View row;
        ListMapDestinationAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListMapDestinationAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListMapDestinationAdapter.ViewHolder) row.getTag();
        }

        StopPoint stopPoint = list.get(position);
        if(position==positionChecked){
            holder.aSwitch.setChecked(true);
        }
        else{
            holder.aSwitch.setChecked(false);
        }
        holder.tvStopPointName.setText(stopPoint.getName());
//        holder.tvStopPointService.setText(listSerVice.get(stopPoint.getServiceTypeId() - 1));
//        holder.tvStopPointMinCost.setText(stopPoint.getMinCost() + " VND");
//        holder.tvStopPointMaxCost.setText(stopPoint.getMaxCost() + " VND");
//        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
//        cal.setTimeInMillis(stopPoint.getArrivalAt());
//        String date1 = DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
//        holder.tvStopPointArriveAt.setText(date1);
//        cal.setTimeInMillis(stopPoint.getLeaveAt());
//        String date2 = DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
//        holder.tvStopPointLeaveAt.setText(date2);

        holder.aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                positionChecked=position;
                notifyDataSetChanged();
                ((MapFragment)fragment).drawRouteToStopPoint(stopPoint);
            }
        });

        holder.showDestinationInfo.setOnClickListener(v -> {
            ((MapFragment)fragment).showStopPointInfo(stopPoint);
        });


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
}