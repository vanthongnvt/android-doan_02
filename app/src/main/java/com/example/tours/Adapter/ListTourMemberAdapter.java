package com.example.tours.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tours.Model.TourMember;
import com.example.tours.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListTourMemberAdapter extends ArrayAdapter<TourMember> {
    private Context context;
    private Integer resource;
    private List<TourMember> list;

    public ListTourMemberAdapter(@NonNull Context context, int resource, @NonNull List<TourMember> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;

    }

    private static class ViewHolder {

        private final TextView tvMemberName;
        private final ImageView imgMemberAvatar;
        private final TextView tvMemberPhone;
        private final TextView tvHost;

        private ViewHolder(View row) {
            tvMemberName = row.findViewById(R.id.tv_member_name);
            imgMemberAvatar=row.findViewById(R.id.member_avatar);
            tvMemberPhone=row.findViewById(R.id.tv_member_phone);
            tvHost=row.findViewById(R.id.tv_host);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ListTourMemberAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListTourMemberAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListTourMemberAdapter.ViewHolder) row.getTag();
        }

        TourMember tourMember = list.get(position);
        if(tourMember.getAvatar()!=null){
            Picasso.get().load(tourMember.getAvatar()).into(holder.imgMemberAvatar);
        }

        holder.tvMemberName.setText(tourMember.getName());
        holder.tvMemberPhone.setText(tourMember.getPhone());
        if(tourMember.getIsHost()){
            holder.tvHost.setText(R.string.host);
        }
        return row;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public TourMember getItem(int position) {
        return list.get(position);
    }

}
