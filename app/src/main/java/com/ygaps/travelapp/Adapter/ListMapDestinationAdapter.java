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

import com.ygaps.travelapp.FollowTourActivity;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.R;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListMapDestinationAdapter extends ArrayAdapter<StopPoint> {
    private Context context;
    private Integer resource;
    private List<StopPoint> list;
    private List<String> listSerVice;
    private int positionChecked=-1;
    private Fragment fragment;

    public ListMapDestinationAdapter(@NonNull Context context, int resource, @NonNull List<StopPoint> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        listSerVice = Arrays.asList("Restaurant", "Hotel", "Rest Station", "Other");
    }

    private static class ViewHolder {

        private final TextView tvStopPointName;

        private final Switch aSwitch;
        private final ImageView showDestinationInfo;

        //code

        private ViewHolder(View row) {
            tvStopPointName = row.findViewById(R.id.tv_stop_point_name);
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
        if(!(position==positionChecked)){
            holder.aSwitch.setChecked(false);
        }
        holder.tvStopPointName.setText(stopPoint.getName());

        holder.aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                positionChecked=position;
                notifyDataSetChanged();
                ((FollowTourActivity)context).drawRouteToStopPoint(stopPoint);
            }
            else{
                positionChecked=-1;
                notifyDataSetChanged();
                ((FollowTourActivity)context).removeRouteToStopPoint();
            }
        });

        holder.showDestinationInfo.setOnClickListener(v -> {
            ((FollowTourActivity)context).showStopPointInfo(stopPoint);
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