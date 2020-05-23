package com.example.bluetooth;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class BlueListAdapter extends RecyclerView.Adapter<BlueListAdapter.BlueViewHolder> {
    private List<BlueDevice> mBlueList;
    public BlueListAdapter(List<BlueDevice> blueDevicesList){
        mBlueList = blueDevicesList;
    }
    @NonNull
    @Override
    public BlueViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bluetooth_item,parent,false);
        BlueViewHolder holder = new BlueViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BlueViewHolder holder, int position) {
        BlueDevice device = mBlueList.get(position);
        holder.device_name.setText(device.getDeviceName());
        holder.device_mac.setText(device.getDeviceMac());
        holder.device_rssi.setText(device.getRSSI() + "db");
    }

    @Override
    public int getItemCount() {
        return mBlueList.size();
    }

    public class BlueViewHolder extends RecyclerView.ViewHolder{
        TextView device_name;
        TextView device_mac;
        TextView device_rssi;

        public BlueViewHolder(@NonNull View itemView) {
            super(itemView);
            device_name = (TextView) itemView.findViewById(R.id.device_name);
            device_mac = (TextView) itemView.findViewById(R.id.device_mac);
            device_rssi = (TextView) itemView.findViewById(R.id.device_rssi);
        }
    }
}
