package com.cumtips.android.Utils;

import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientThread implements Runnable {
    private Socket s;
    private WebView view;
    BufferedReader br = null;

    public ClientThread(Socket s,WebView view) throws IOException{
        this.s = s;
        this.view = view;
        br = new BufferedReader(new InputStreamReader(s.getInputStream()));
    }
    @Override
    public void run() {
        try{
            String content = null;
            boolean flag = true;
            while((content = br.readLine())!=null) {
                if(content.isEmpty()){
                    continue;
                }
                if(flag && !vueWebChromeClient.idList.isEmpty()){
                    for(String idstr:vueWebChromeClient.idList){
                        System.out.println("车位编号：" + idstr);
                        final int idnum = Integer.parseInt(idstr);
                        view.post(new Runnable() {
                            @Override
                            public void run() {
                                view.evaluateJavascript("javascript:setidList(" + idnum + ")", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        System.out.println(value);
                                    }
                                });
                                view.evaluateJavascript("javascript:setPosIconById(" + idnum + ")", new ValueCallback<String>() {
                                    @Override
                                    public void onReceiveValue(String value) {
                                        System.out.println(value);
                                    }
                                });
                            }
                        });
                    }
                    flag = false;
                }
                final int id = Integer.parseInt(content);
                System.out.println("id :" + id);
                view.post(new Runnable() {
                    @Override
                    public void run() {
                        view.evaluateJavascript("javascript:setidList(" + id + ")", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(String value) {
                                System.out.println(value);
                            }
                        });
                    }
                });
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void setPositionIcon(){
        pysvm.getPatkingNum();
    }
}
