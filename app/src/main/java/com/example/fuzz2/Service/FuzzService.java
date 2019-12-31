package com.example.fuzz2.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.fuzz2.MainActivity;
import com.example.fuzz2.util.BinderInfo;
import com.example.fuzz2.util.FuzzThread;
import com.example.fuzz2.util.FuzzUtil;
import com.example.fuzz2.util.LogUtils;

public class FuzzService extends Service {
  private static final String TAG = "SECNEO";
  private BinderInfo binderInfo;
  private String[] services;
  private String[] allInterfaceName;
  private LogUtils logu;
  private FuzzUtil fuzzu;
  @Override
  public void onCreate() {
    super.onCreate();
    binderInfo = new BinderInfo(FuzzService.this);
    fuzzu = FuzzUtil.getInstance(FuzzService.this);
    logu = binderInfo.getLogu();
    services = binderInfo.getServices();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
  }

  @Override
  public int onStartCommand(Intent intent, int flags, int startId) {


    Log.d(TAG, "FUZZ START ..........................");
    logu.log("FUZZ START ...............................");

    int thNum = 10;
    int ServiceNum = services.length;
    int mod = ServiceNum % thNum;
    int n = (int) ServiceNum / thNum;
    int start = 0;
    int end = 0;
    Thread T = null;
    String[] tmp;
    FuzzThread[] a = new FuzzThread[thNum];
    FuzzThread th = null;
    for (int i = 1; i <= thNum; i++) {
      if (mod == 0) {
        tmp = fuzzu.splitStringArray(services, start, start + n);
        th = new FuzzThread(FuzzService.this, tmp, i);
        a[i - 1] = th;
        start = start + n;
      } else {

        if (i == thNum) {
          tmp = fuzzu.splitStringArray(services, start, start + mod);
          th = new FuzzThread(FuzzService.this, tmp, i);
          a[i - 1] = th;
          start = start + n;
          break;
        }
        tmp = fuzzu.splitStringArray(services, start, start + n);
        th = new FuzzThread(FuzzService.this, tmp, i);
        a[i - 1] = th;
        start = start + n;
      }


    }

    for (FuzzThread t : a) {
      T = new Thread(t);
      T.start();
    }
    return super.onStartCommand(intent, flags, startId);
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
}
