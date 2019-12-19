package com.ygaps.travelapp.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.media.Rating;
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
import com.ygaps.travelapp.Model.MessageResponse;
import com.ygaps.travelapp.Model.SpinnerReportItem;
import com.ygaps.travelapp.Model.TourReview;
import com.ygaps.travelapp.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListTourReviewAdapter extends ArrayAdapter<TourReview> {
    private Context context;
    private Integer resource;
    private List<TourReview> listTourReview;

    public ListTourReviewAdapter(@NonNull Context context, int resource, @NonNull List<TourReview> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.listTourReview = objects;
    }

    private static class ViewHolder{
        private ImageView avatar;
        private TextView name;
        private RatingBar ratingBar;
        private TextView date;
        private TextView review;
        private Spinner report;

        private ViewHolder(View row){
            avatar = (ImageView)row.findViewById(R.id.lv_tourreview_item_avatar);
            name = (TextView) row.findViewById(R.id.lv_tourreview_item_name);
            ratingBar = (RatingBar) row.findViewById(R.id.lv_tourreview_item_ratingbar);
            date = (TextView) row.findViewById(R.id.lv_tourreview_item_date_created);
            review = (TextView) row.findViewById(R.id.lv_tourreview_item_review);
            report = (Spinner) row.findViewById(R.id.spn_report);
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
            holder = new ListTourReviewAdapter.ViewHolder(row);
            row.setTag(holder);
        } else {
            row = convertView;
            holder = (ListTourReviewAdapter.ViewHolder) row.getTag();
        }

        TourReview item = listTourReview.get(position);

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
        holder.review.setText(item.getReview());

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
                    content.setText("Báo cáo review này ?");
                    Button btnOk = (Button) dialog.findViewById(R.id.report_ok);
                    Button btnCancel = (Button) dialog.findViewById(R.id.report_cancel);

                    btnOk.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            APITour apiTour = new APIRetrofitCreator().getAPIService();
                            apiTour.reportReviewTour(TokenStorage.getInstance().getAccessToken(), Integer.parseInt(item.getId())).enqueue(new Callback<MessageResponse>() {
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
                }
                holder.report.setSelection(1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                holder.report.setSelection(1);
            }
        });


        return row;
    }
}
