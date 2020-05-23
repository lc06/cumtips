package com.cumtips.android.Utils;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.cumtips.android.BlueToothUtil.BlueDevice;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.List;

public class pysvm {
    public static List<BlueDevice> devicesList;
    private static int id;

    public static int getPatkingNum(){
        Gson gson = new GsonBuilder().create();
        String devicesJson = gson.toJson(devicesList);
        /*
        PyObject sum = null;
        try {
            PythonInterpreter pyInterp = new PythonInterpreter();
            pyInterp.set("count",5);
            pyInterp.execfile("pysvm.py");
            sum = pyInterp.get("sum");
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println(sum);
         */
        Python py = Python.getInstance();
        PyObject obj = py.getModule("svmsplit").callAttr("svm_train",devicesJson);
        id = obj.toJava(int.class);
        return id;
    }
}
