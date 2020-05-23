package com.cumtips.android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.cumtips.android.BlueToothUtil.BleAdapter;
import com.cumtips.android.BlueToothUtil.BlueDevice;
import com.cumtips.android.Utils.pysvm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.interfaces.OnInputConfirmListener;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class BlueToothActivity extends AppCompatActivity {

    private List<BlueDevice> mBleList = new ArrayList<>();
    private BluetoothAdapter bluetoothAdapter;  //声明一个蓝牙适配器对象
    private BleAdapter bleListAdapter;
    private int REQUEST_ENABLE_BT = 1;
    private int REQUEST_ENABLE_LC = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blue_tooth);
        this.setTitle("蓝牙设备搜索");
        Toolbar toolbar = (Toolbar) findViewById(R.id.bluetooth_toolbar);
        setSupportActionBar(toolbar);
        getPermission();
        //initDevices();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.bluedevice_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        bleListAdapter = new BleAdapter(mBleList);
        recyclerView.setAdapter(bleListAdapter);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //添加监听BluetoothAdapter.ACTION_DISCOVERY_FINISHED动作
        registerReceiver(receiver, filter);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                initLocation();
                Toast.makeText(BlueToothActivity.this,"开始搜索周围设备",Toast.LENGTH_SHORT).show();
            }
        });
        initLocation();
        FloatingActionButton enter = (FloatingActionButton) findViewById(R.id.enter);
        enter.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
               Intent intent = new Intent(BlueToothActivity.this,MainActivity.class);
               startActivity(intent);
            }
        });
        initLocation();
    }
    private void getPermission(){
        List<String> permissionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(BlueToothActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(BlueToothActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        /*
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_BACKGROUND_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_BACKGROUND_LOCATION);
        }
         */
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(BlueToothActivity.this,permissions,1);
        }
    }
    private void initLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!gps){
            AlertDialog.Builder dialog= new AlertDialog.Builder (BlueToothActivity.this);
            dialog.setTitle("请打开GPS连接");
            dialog.setMessage("为了提高定位的精确度，更好的为您服务，请打开GPS");
            dialog.setCancelable(false);
            dialog.setPositiveButton("设置", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(intent, REQUEST_ENABLE_LC);
                }
            });
            dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            }) ;
            dialog. show();
        }
        else{
            initBluetooth();
        }
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 2){
            if(resultCode == 0){
                initBluetooth();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    */

    private void initBluetooth(){
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Toast.makeText(this,"本机未找到蓝牙功能",Toast.LENGTH_SHORT).show();
        }
        else {
            if (!bluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
            if(!bluetoothAdapter.isDiscovering()){
                mBleList.clear();//开始搜索之前先清空蓝牙设备列表
                bleListAdapter.notifyDataSetChanged();
                bluetoothAdapter.startDiscovery();
                Toast.makeText(this, "开始搜索周围设备", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /*
    private void initDevices(){
        for(int i = 0;i < 6;i++){
            BlueDevice device = new BlueDevice(String.valueOf(i),"1f:34:12:45:4d",-89);
            mBleList.add(device);
        }
    }
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean flag = true;
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(BlueDevice blueDevice : mBleList){
                    if(blueDevice.getDevice_addr().equals(device.getAddress())){
                        flag = false; //设置不保存搜索到的重复蓝牙设备
                    }
                }
                if(flag){
                    String deviceName = device.getName();
                    if(deviceName==null){
                        deviceName = "设备名为空";
                    }
                    String deviceAddress = device.getAddress(); // MAC address
                    int deviceRssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI); //获取搜索到的蓝牙设备信号强度
                    mBleList.add(new BlueDevice(deviceName,deviceAddress,deviceRssi));
                    bleListAdapter.notifyItemInserted(mBleList.size()-1);
                    Toast.makeText(getApplicationContext(),"当前设备数: " + mBleList.size(),Toast.LENGTH_SHORT).show();
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { // 搜索完毕
                Toast.makeText(getApplicationContext(),"蓝牙设备搜索完成",Toast.LENGTH_SHORT).show();
                savedevices(mBleList);
            }
        }
    };

    private void savedevices(final List<BlueDevice> blueDevices){
        new XPopup.Builder(this)
                .autoOpenSoftInput(true)
                .isRequestFocus(false)
                .asInputConfirm("提示", "请输入车位号", new OnInputConfirmListener() {
                    @Override
                    public void onConfirm(String text) {
                        savedata(blueDevices,text);
                    }
                })
                .show();
    }
    private void savedata(List<BlueDevice> blueDevices,String text){
        if(!blueDevices.isEmpty()){
            pysvm.devicesList = blueDevices;
            String privatePath = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString();
            //Toast.makeText(getApplicationContext(),privatePath,Toast.LENGTH_SHORT).show();
            try{
                File filename = new File(privatePath,"蓝牙信号.txt");
                FileWriter fw = new FileWriter(filename,true);
                fw.write("停车位: "+text+"\n");
                for(int i=0;i<blueDevices.size();i++){
                    String deviceAddr = blueDevices.get(i).getDevice_addr();
                    int deviceRssi = blueDevices.get(i).getDevice_rssi();
                    fw.write("MAC地址: "+deviceAddr+" | 信号强度: "+deviceRssi+"\n");
                }
                Toast.makeText(getApplicationContext(),"存储成功",Toast.LENGTH_SHORT).show();
                fw.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }
}
