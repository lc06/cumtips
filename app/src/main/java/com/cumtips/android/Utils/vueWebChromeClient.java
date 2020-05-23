package com.cumtips.android.Utils;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;

import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class vueWebChromeClient extends WebChromeClient {
    private Socket s;
    private PrintStream ps;
    public static List<String> idList = new ArrayList<>();
    @Override
    public void onProgressChanged(final WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
        if(newProgress == 100){
            System.out.println("加载完成！");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        s = new Socket("47.115.16.33", 30000);
                        new Thread(new ClientThread(s, view)).start();
                        ps = new PrintStream(s.getOutputStream());
                        ps.println("request");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public boolean onJsConfirm(final WebView view, String url, final String message, final JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("确认提示")
                .setMessage(message)
                .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                        view.evaluateJavascript("javascript:getidplaceholder()", new ValueCallback<String>() {
                            @Override
                            public void onReceiveValue(final String value) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            //Socket s = new Socket("47.115.16.33", 30000);
                                            //new Thread(new ClientThread(s,view)).start();
                                            //PrintStream ps = new PrintStream(s.getOutputStream());
                                            ps.println(value);
                                            idList.add(value);
                                        }
                                        catch (Exception e){
                                            new AlertDialog.Builder(view.getContext())
                                                    .setTitle("错误!")
                                                    .setMessage(e.toString()).show();
                                        }
                                    }
                                }).start();
                            }
                        });
                    }
                })
                .setNeutralButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.cancel();
                    }
                })
                .show();
        return true;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        new AlertDialog.Builder(view.getContext())
                .setTitle("提示")
                .setMessage(message).show();
        result.confirm();
        return true;
    }
}
