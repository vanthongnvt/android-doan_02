package com.ygaps.travelapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;
import com.ygaps.travelapp.ApiService.APIRetrofitCreator;
import com.ygaps.travelapp.ApiService.APITour;
import com.ygaps.travelapp.AppHelper.TokenStorage;
import com.ygaps.travelapp.Model.FeedbackService;
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.SpinnerReportItem;
import com.ygaps.travelapp.Model.TourReview;
import com.ygaps.travelapp.R;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.net.ssl.SNIHostName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListServiceFeedbackAdapter extends ArrayAdapter<FeedbackService> {
    private Context context;
    private Integer resource;
    private List<FeedbackService> listFeedbackService;

    public ListServiceFeedbackAdapter(@NonNull Context context, int resource, @NonNull List<FeedbackService> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listFeedbackService = objects;
    }

    private static class ViewHolder{
        private ImageView avatar;
        private TextView name;
        private RatingBar ratingBar;
        private TextView date;
        private TextView feedback;
        private TextView phone;
        private Spinner report;

        private ViewHolder(View row){
            avatar = (ImageView)row.findViewById(R.id.lv_feedbackservice_item_avatar);
            name = (TextView) row.findViewById(R.id.lv_feedbackservice_item_name);
            ratingBar = (RatingBar) row.findViewById(R.id.lv_feedbackservice_item_ratingbar);
            date = (TextView) row.findViewById(R.id.lv_feedbackservice_item_date_created);
            feedback = (TextView) row.findViewById(R.id.lv_feedbackservice_item_review);
            phone = (TextView) row.findViewById(R.id.lv_feedbackservice_item_phone);
            report = (Spinner) row.findViewById(R.id.spn_feedback_report);
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
            holder = new ListServiceFeedbackAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListServiceFeedbackAdapter.ViewHolder) row.getTag();
        }

        FeedbackService item = listFeedbackService.get(position);

        if (item.getAvatar() != null && !item.getAvatar().isEmpty()){
            Picasso.get().load(item.getAvatar()).into(holder.avatar);
        }else{
            holder.avatar.setImageResource(R.drawable.unknown_user);
        }
        holder.name.setText(item.getName());
        holder.ratingBar.setRating(item.getPoint());
        Calendar cal = Calendar.getInstance();
        long time = 0;
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        if(item.getCreatedOn() == null){
            holder.date.setText("00/00/0000");
        }
        else{
            time = Long.parseLong(item.getCreatedOn());
            cal.setTimeInMillis(time);
            Date timeCreated = cal.getTime();
            holder.date.setText(dateFormat.format(timeCreated));
        }
        holder.feedback.setText(item.getFeedback());
        holder.phone.setText(item.getPhone());

        List<SpinnerReportItem> spnList = new ArrayList<>();
        spnList.add(new SpinnerReportItem("Báo cáo", R.drawable.ic_warning));
        spnList.add(new SpinnerReportItem("Quay lại", R.drawable.ic_back));
        SpinnerReportAdapter spnAdapter = new SpinnerReportAdapter(getContext(), R.layout.spinner_report_row, spnList);
        spnAdapter.notifyDataSetChanged();
        holder.report.setAdapter(spnAdapter);

        holder.report.setSelection(1);
        holder.report.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0){
                    Dialog dialog = new Dialog(row.getContext());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_confirm_reporting);

                    TextView content = (TextView) dialog.findViewById(R.id.report_content);
                    content.setText("Báo cáo feedback này ?");
                    Button btnOk = (Button) dialog.findViewById(R.id.report_ok);
                    Button btnCancel = (Button) dialog.findViewById(R.id.report_cancel);

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            APITour apiTour = new APIRetrofitCreator().getAPIService();
                            apiTour.reportFeedbackService(TokenStorage.getInstance().getAccessToken(), item.getId()).enqueue(new Callback<MessageResponse>() {
                                @Override
                                public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                                    if(response.isSuccessful()){
                                        Toast.makeText(context, "Báo cáo thành công", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                    else if(response.code() == 500){
                                        Toast.makeText(getContext(), "Lỗi server, báo cáo thất bại", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<MessageResponse> call, Throwable t) {
                                    Toast.makeText(getContext(), R.string.failed_fetch_api, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                    holder.report.setSelection(1);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                holder.report.setSelection(1);
            }
        });


        return row;
    }
}
