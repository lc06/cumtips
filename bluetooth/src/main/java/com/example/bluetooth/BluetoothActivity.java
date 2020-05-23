package com.cumtlbs.bluetooth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class BluetoothActivity extends AppCompatActivity {

    private List<BlueDevice> mBlueDevices = new ArrayList<>(); //创建一个List泛型集合用来存放搜索到的蓝牙设备
    private BluetoothAdapter mBluetooth;
    private BlueListAdapter mListAdapter;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private int mOpenCode = 1;
    private IntentFilter discoveryFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view); //实例化recyclerview列表控件
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh); //实例化下拉刷新控件
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary); //设置下拉刷新进度条的颜色
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() { //设置下拉刷新操作的监听器
            @Override
            public void onRefresh() {
                refreshBlueDevices(); //当发生下拉刷新操作时，调用该方法
            }
        });
        initBluetooth(); //初始化蓝牙适配器
        initBlueDevices(); //初始化蓝牙设备列表
        discoveryFilter = new IntentFilter();
        discoveryFilter.addAction(BluetoothDevice.ACTION_FOUND); //添加监听BluetoothDevice.ACTION_FOUND动作
        discoveryFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED); //添加监听BluetoothAdapter.ACTION_DISCOVERY_FINISHED动作
        registerReceiver(discoveryReceiver,discoveryFilter); //注册本地广播监听器
    }
    private void refreshBlueDevices(){
        new Thread(new Runnable() {
            @Override
            public void run() { //创建新线程
                try{
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        initBlueDevices(); //刷新蓝牙设备列表
                        mListAdapter.notifyDataSetChanged(); //通知recyclerview绑定的数据已经发生改变，展示改变后的蓝牙设备列表
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void initBluetooth(){
        // Android从4.3开始增加支持BLE技术（即蓝牙4.0及以上版本）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            // 从系统服务中获取蓝牙管理器
            BluetoothManager bm = (BluetoothManager)
                    getSystemService(Context.BLUETOOTH_SERVICE);
            mBluetooth = bm.getAdapter();
        } else {
            // 获取系统默认的蓝牙适配器
            mBluetooth = BluetoothAdapter.getDefaultAdapter();
        }
        if (mBluetooth == null) {
            Toast.makeText(this, "本机未找到蓝牙功能", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    private void initBlueDevices(){
        mBlueDevices.clear(); //清空mBlueDevices集合中保存的的蓝牙设备
        if(!mBluetooth.isEnabled()){
            mBluetooth.enable(); //启动蓝牙功能
        }
        //弹出是否允许扫描蓝牙设备的选择对话框
        Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        startActivityForResult(intent, mOpenCode);
        if(!mBluetooth.isDiscovering()){
            mBluetooth.startDiscovery(); //开始搜索周围的蓝牙设备
        }
        if (mListAdapter == null) { // 首次打开页面，则创建一个新的蓝牙设备列表
            mListAdapter = new BlueListAdapter(mBlueDevices);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(mListAdapter);
        } else { // 不是首次打开页面，则刷新蓝牙设备列表
            mListAdapter.notifyDataSetChanged();
        }
    }
    // 蓝牙设备的搜索结果通过广播返回
    private BroadcastReceiver discoveryReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            boolean flag = true;
            // 获得已经搜索到的蓝牙设备
            if (action.equals(BluetoothDevice.ACTION_FOUND)) { // 发现新的蓝牙设备
                Toast.makeText(getApplicationContext(),"发现新的蓝牙设备",Toast.LENGTH_SHORT).show();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                for(BlueDevice blueDevice : mBlueDevices){
                    if(blueDevice.getDeviceMac().equals(device.getAddress())){
                        flag = false; //设置不保存搜索到的重复蓝牙设备
                    }
                }
                if(flag){
                    int rssi = intent.getExtras().getShort(BluetoothDevice.EXTRA_RSSI); //获取搜索到的蓝牙设备信号强度
                    mBlueDevices.add(new BlueDevice(device.getName(),device.getAddress(),rssi)); //将搜索到的蓝牙设备添加进mBlueDevices集合
                }
                mListAdapter.notifyItemInserted(mBlueDevices.size()-1); //在recyclerview列表的最后插入搜索到的蓝牙设备
                Toast.makeText(getApplicationContext(),"当前设备数: " + mBlueDevices.size(),Toast.LENGTH_SHORT).show();
            } else if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) { // 搜索完毕
                Toast.makeText(getApplicationContext(),"蓝牙设备搜索完成",Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(discoveryReceiver);
    }
}
