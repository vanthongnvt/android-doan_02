package com.ygaps.travelapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ygaps.travelapp.Model.User;
import com.ygaps.travelapp.R;
import com.ygaps.travelapp.ui.main.TourMemberFragment;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListSearchMemberAdapter  extends ArrayAdapter<User> {
    private Context context;
    private Integer resource;
    private List<User> list;
    private Fragment fragment;

    public ListSearchMemberAdapter(@NonNull Context context, int resource, @NonNull List<User> objects, Fragment fragment) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.list = objects;
        this.fragment=fragment;
    }

    private static class ViewHolder {

        private final TextView tvMemberName;
        private final ImageView imgMemberAvatar;
        private final TextView tvMemberPhone;
        private final ImageView btnInvite;
        private ViewHolder(View row) {
            tvMemberName = row.findViewById(R.id.tv_member_name);
            imgMemberAvatar=row.findViewById(R.id.member_avatar);
            tvMemberPhone=row.findViewById(R.id.tv_member_phone);
            btnInvite=row.findViewById(R.id.invite_member);
        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ListSearchMemberAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListSearchMemberAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListSearchMemberAdapter.ViewHolder) row.getTag();
        }

        User user = list.get(position);
        if(user.getAvatar()!=null&&!user.getAvatar().isEmpty()){
            Picasso.get().load(user.getAvatar()).into(holder.imgMemberAvatar);
        }
        else{
            holder.imgMemberAvatar.setImageResource(R.drawable.unknown_user);
        }

        holder.tvMemberName.setText(user.getFullName());
        holder.tvMemberPhone.setText(user.getPhone());
        holder.btnInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TourMemberFragment)fragment).inviteMember(user);
            }
        });
        return row;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public User getItem(int position) {
        return list.get(position);
    }

}
