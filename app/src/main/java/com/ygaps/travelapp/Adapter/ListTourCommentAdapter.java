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

import com.ygaps.travelapp.Model.TourComment;
import com.ygaps.travelapp.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListTourCommentAdapter extends ArrayAdapter<TourComment> {
    private Context context;
    private Integer resource;
    private List<TourComment> list;

    public ListTourCommentAdapter(@NonNull Context context, int resource, @NonNull List<TourComment> objects) {
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
        ListTourCommentAdapter.ViewHolder holder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(resource, parent, false);
            holder = new ListTourCommentAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListTourCommentAdapter.ViewHolder) row.getTag();
        }

        TourComment tourMember = list.get(position);
        if(tourMember.getAvatar()!=null&&!tourMember.getAvatar().isEmpty()){
            Picasso.get().load(tourMember.getAvatar()).into(holder.imgMemberAvatar);
        }

        holder.tvMemberName.setText(tourMember.getName());
        holder.tvMemberComment.setText(tourMember.getComment());
        holder.tvCommentTime.setText(tourMember.getCreatedOn());
        return row;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public TourComment getItem(int position) {
        return list.get(position);
    }

}
