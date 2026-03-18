package com.hospital.serviceapp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.hospital.serviceapp.R;
import com.hospital.serviceapp.models.Service;
import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private List<Service> serviceList;
    private OnServiceListener listener;

    public interface OnServiceListener {
        void onDelete(Service service);
    }

    public ServiceAdapter(List<Service> serviceList, OnServiceListener listener) {
        this.serviceList = serviceList;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Service service = serviceList.get(position);
        holder.tvCode.setText(service.getCode());
        holder.tvName.setText(service.getName());
        holder.btnDelete.setOnClickListener(v -> listener.onDelete(service));
    }

    @Override
    public int getItemCount() {
        return serviceList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvName;
        ImageButton btnDelete;

        public ViewHolder(View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvServiceCode);
            tvName = itemView.findViewById(R.id.tvServiceName);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}