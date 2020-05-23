package com.cumtips.android.BlueToothUtil;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.cumtips.android.R;

import java.util.List;

public class BleAdapter extends RecyclerView.Adapter<BleAdapter.ViewHolder> {

    private Context mContext;

    private List<BlueDevice> bleList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        TextView deviceName,deviceAddr,deviceRssi;
        public ViewHolder(View view){
            super(view);
            cardView = (CardView) view;
            deviceName = (TextView) view.findViewById(R.id.device_name);
            deviceAddr = (TextView) view.findViewById(R.id.device_addr);
            deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
        }
    }

    public BleAdapter(List<BlueDevice> bleList){
        this.bleList = bleList;
    }

    @NonNull
    @Override
    public BleAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(mContext == null){
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.device_item, parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull BleAdapter.ViewHolder holder, int position) {
        BlueDevice device = bleList.get(position);
        holder.deviceName.setText(device.getDevice_name());
        holder.deviceAddr.setText(device.getDevice_addr());
        holder.deviceRssi.setText(String.valueOf(device.getDevice_rssi()));
    }

    @Override
    public int getItemCount() {
        return bleList.size();
    }
}
