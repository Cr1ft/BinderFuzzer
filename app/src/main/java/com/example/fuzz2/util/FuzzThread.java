package com.example.fuzz2.util;

import android.content.Context;
import android.util.Log;

import com.example.fuzz2.util.BinderInfo;

public class FuzzThread implements Runnable {

    private String [] mService;
    private Context mContext;
    private BinderInfo binderInfo;
    private static final String TAG = "SECNEO";
    private int thnum;
    public FuzzThread(Context context,String[] serviceArray,int num){
        mService = serviceArray;
        mContext = context;
        thnum=num;
        binderInfo = new BinderInfo(mContext);
    }

    @Override
    public void run() {
        Log.w(TAG,"---------------[Thread"+Integer.toString(thnum)+"Start]");
        if (mService == null) {
            Log.e(TAG,"services is null.");
            return;
        }
        for(String sv : mService) {
            try {
                binderInfo.fuzz(sv);
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
        Log.w(TAG,"===================[Thread"+Integer.toString(thnum)+"END]=========================");


    }

    public void start(){
        Thread th = new Thread(this);
        th.start();
    }
}
