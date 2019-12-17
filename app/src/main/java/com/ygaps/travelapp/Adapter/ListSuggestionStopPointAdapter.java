package com.ygaps.travelapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.ygaps.travelapp.Model.StopPoint;
import com.ygaps.travelapp.R;

public class ListSuggestionStopPointAdapter extends SuggestionsAdapter<StopPoint, ListSuggestionStopPointAdapter.SuggestionViewHolder> {
    private SuggestionsAdapter.OnItemViewClickListener listener;
    public ListSuggestionStopPointAdapter(LayoutInflater inflater) {
        super(inflater);
    }

    @Override
    public void onBindSuggestionHolder(StopPoint stopPoint, SuggestionViewHolder suggestionViewHolder, int i) {
        suggestionViewHolder.tvStopPointName.setText(stopPoint.getName());
        suggestionViewHolder.tvStopPointAddress.setText(stopPoint.getAddress());
    }

    @Override
    public int getSingleViewHeight() {
        return 15;
    }

    public void setListener(SuggestionsAdapter.OnItemViewClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = getLayoutInflater().inflate(R.layout.list_view_suggest_stop_point_item, parent, false);
        return new SuggestionViewHolder(view);
    }

     class SuggestionViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvStopPointName;
        private final TextView tvStopPointAddress;


        private SuggestionViewHolder(View row) {
            super(row);
            tvStopPointName = row.findViewById(R.id.name);
            tvStopPointAddress=row.findViewById(R.id.address);
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    v.setTag(getSuggestions().get(getAdapterPosition()));
                    listener.OnItemClickListener(getAdapterPosition(),v);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return getSuggestions().size();
    }

    public interface OnItemViewClickListener{
        void OnItemClickListener(int position,View v);
        void OnItemDeleteListener(int position,View v);
    }


}
