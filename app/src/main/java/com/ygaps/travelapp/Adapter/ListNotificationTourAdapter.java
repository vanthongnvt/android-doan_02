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

import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.Model.Notification;
import com.ygaps.travelapp.R;

import java.util.List;

public class ListNotificationTourAdapter extends ArrayAdapter<Notification> {
        private Context context;
        private Integer resource;
        private List<Notification> list;

        public ListNotificationTourAdapter(@NonNull Context context, int resource, @NonNull List<Notification> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.list = objects;

        }

private static class ViewHolder {

    private final TextView tvMemberName;
    private final ImageView imgMemberAvatar;
    private final TextView tvMemberComment;
    private final TextView tvCommentTime;

    private ViewHolder(View row) {
        tvMemberName = row.findViewById(R.id.tv_member_name);
        imgMemberAvatar=row.findViewById(R.id.member_avatar);
        tvMemberComment=row.findViewById(R.id.tv_comment_content);
        tvCommentTime= row.findViewById(R.id.tv_comment_time);
    }
}

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row;
        ListNotificationTourAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListNotificationTourAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListNotificationTourAdapter.ViewHolder) row.getTag();
        }

        Notification tourMember = list.get(position);
        if(tourMember.getAvatar()!=null&&!tourMember.getAvatar().isEmpty()){
            Picasso.get().load(tourMember.getAvatar()).into(holder.imgMemberAvatar);
        }
        else{
            holder.imgMemberAvatar.setImageResource(R.drawable.unknown_user);
        }

        holder.tvMemberName.setText(tourMember.getName());
        holder.tvMemberComment.setText(tourMember.getNotification());
        holder.tvCommentTime.setText(null);
        return row;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Notification getItem(int position) {
        return list.get(position);
    }

}
