package com.hospital.serviceapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.serviceapp.R;
import com.hospital.serviceapp.models.Request;
import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private List<Request> requestList;
    private OnRequestListener listener;

    public interface OnRequestListener {
        void onDelete(Request request);
    }

    public RequestAdapter(List<Request> requestList, OnRequestListener listener) {
        this.requestList = requestList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Request request = requestList.get(position);

        holder.tvPatient.setText("Patient: " + request.getUsername());
        holder.tvService.setText("Service: " + request.getServiceName());
        holder.tvLocation.setText("Ward: " + request.getWardNumber() + ", Bed: " + request.getBedNumber());
        holder.tvNotes.setText("Notes: " + request.getNotes());
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvTime.setText(request.getTimestamp());

        holder.btnDelete.setOnClickListener(v -> listener.onDelete(request));
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatient, tvService, tvLocation, tvNotes, tvStatus, tvTime;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvPatient = itemView.findViewById(R.id.tvPatient);
            tvService = itemView.findViewById(R.id.tvService);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvNotes = itemView.findViewById(R.id.tvNotes);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvTime = itemView.findViewById(R.id.tvTime);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}