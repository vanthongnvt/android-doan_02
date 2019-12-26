package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.ygaps.travelapp.HomeActivity;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.ui.main.ServiceReviewActivity;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ListStopPointAdapter extends ArrayAdapter<StopPoint> {
    private Context context;
    private Integer resource;
    private List<StopPoint> list;
    private List<String> listSerVice;
    private List<String> listProvince;

    public static String STOPPOINT_ID = "STOPPOINT_ID";

    public ListStopPointAdapter(@NonNull Context context, int resource, @NonNull List<StopPoint> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;

        listSerVice = Arrays.asList("Nhà hàng", "Khách sạn", "Trạm nghỉ", "Khác");
        listProvince = Arrays.asList("Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Bình Dương", "Đồng Nai", "Khánh Hòa", "Hải Phòng", "Long An", "Quảng Nam", "Bà Rịa Vũng Tàu", "Đắk Lắk", "Cần Thơ", "Bình Thuận  ", "Lâm Đồng", "Thừa Thiên Huế", "Kiên Giang", "Bắc Ninh", "Quảng Ninh", "Thanh Hóa", "Nghệ An", "Hải Dương", "Gia Lai", "Bình Phước", "Hưng Yên", "Bình Định", "Tiền Giang", "Thái Bình", "Bắc Giang", "Hòa Bình", "An Giang", "Vĩnh Phúc", "Tây Ninh", "Thái Nguyên", "Lào Cai", "Nam Định", "Quảng Ngãi", "Bến Tre", "Đắk Nông", "Cà Mau", "Vĩnh Long", "Ninh Bình", "Phú Thọ", "Ninh Thuận", "Phú Yên", "Hà Nam", "Hà Tĩnh", "Đồng Tháp", "Sóc Trăng", "Kon Tum", "Quảng Bình", "Quảng Trị", "Trà Vinh", "Hậu Giang", "Sơn La", "Bạc Liêu", "Yên Bái", "Tuyên Quang", "Điện Biên", "Lai Châu", "Lạng Sơn", "Hà Giang", "Bắc Kạn", "Cao Bằng");

    }

    private static class ViewHolder {

        private final TextView tvStopPointName;
        private final TextView tvStopPointMinCost;
        private final TextView tvStopPointMaxCost;
        private final TextView tvStopPointArriveAt;
        private final TextView tvStopPointLeaveAt;
        private final TextView tvStopPointService;
        private final TextView btnToStopPointReview;
        private final TextView tvProvince;
        private final TextView tvAddress;

        private ViewHolder(View row) {
            tvStopPointName = row.findViewById(R.id.tv_stop_point_name);
            tvStopPointMinCost=row.findViewById(R.id.tv_stop_point_min_cost);
            tvStopPointMaxCost=row.findViewById(R.id.tv_stop_point_max_cost);
            tvStopPointArriveAt=row.findViewById(R.id.tv_stop_point_startDate);
            tvStopPointLeaveAt=row.findViewById(R.id.tv_stop_point_endDate);
            tvStopPointService=row.findViewById(R.id.tv_stop_point_service);
            btnToStopPointReview = row.findViewById(R.id.btn_stop_point_review);
            tvProvince = row.findViewById(R.id.stop_point_info_province_city);
            tvAddress = row.findViewById(R.id.stop_point_info_address);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
//        return super.getView(position, convertView, parent);
        View row;
        ListStopPointAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListStopPointAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListStopPointAdapter.ViewHolder) row.getTag();
        }

        StopPoint stopPoint = list.get(position);
        holder.tvStopPointName.setText(stopPoint.getName());
        if (stopPoint.getServiceTypeId() >= 1 && stopPoint.getServiceTypeId() <= 4) {
            holder.tvStopPointService.setText(listSerVice.get(stopPoint.getServiceTypeId() - 1));
        }
        if (stopPoint.getProvinceId() >= 1 && stopPoint.getProvinceId() <= 64) {
            holder.tvProvince.setText(listProvince.get(stopPoint.getProvinceId() - 1));
        }

        holder.tvAddress.setText(stopPoint.getAddress());

        holder.tvStopPointMinCost.setText(stopPoint.getMinCost() + " VND");
        holder.tvStopPointMaxCost.setText(stopPoint.getMaxCost() + " VND");
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        if (stopPoint.getArrivalAt() != null){
            cal.setTimeInMillis(stopPoint.getArrivalAt());
            String date1 = DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
            holder.tvStopPointArriveAt.setText(date1);
        }
        if(stopPoint.getLeaveAt() != null) {
            cal.setTimeInMillis(stopPoint.getLeaveAt());
            String date2 = DateFormat.format("HH:mm dd/MM/yyyy", cal).toString();
            holder.tvStopPointLeaveAt.setText(date2);
        }
        holder.btnToStopPointReview.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            Intent intent = new Intent(context, ServiceReviewActivity.class);
            if(context instanceof HomeActivity) {
                bundle.putInt(STOPPOINT_ID, stopPoint.getId());
            }
            else{
                bundle.putInt(STOPPOINT_ID, stopPoint.getServiceId());
            }

            bundle.putSerializable("STOP_POINT",stopPoint);
            intent.putExtras(bundle);
            context.startActivity(intent);
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
