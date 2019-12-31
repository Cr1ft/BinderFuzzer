package com.example.fuzz2.util;

import android.content.Context;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class BinderInfo {
  private static final String TAG = "SECNEO";
  private LogUtils logu;
  private Context mContext;

  public BinderInfo(Context context) {
    File f = new File(context.getFilesDir(), "log.txt");
    logu = LogUtils.getInstance(f.getAbsolutePath());
    mContext = context;
  }

  public LogUtils getLogu() {
    return this.logu;
  }

  //获取所有运行的services
  public String[] getServices() {
    String[] services = null;
    try {
      services = (String[]) Class.forName("android.os.ServiceManager")
          .getDeclaredMethod("listServices").invoke(null);
    } catch (Exception e) {
    }

    return services;
  }


  //获得对应服务的IBinder 对象
  public static IBinder getIBinder(String service) {
    try {
      return (IBinder) Class.forName("android.os.ServiceManager")
          .getDeclaredMethod("getService", String.class).invoke(null, service);
    } catch (Exception e) {
      //
    }

    return null;
  }

  //利用反射获取对应接口的所有code
  public static HashMap<String, Integer> getBinderCode(String interfaceDescriptor) {
    HashMap<String, Integer> codes = new HashMap<>();

    if (interfaceDescriptor == null)
      return codes;

    try {
      Class<?> cStub = Class
          .forName(interfaceDescriptor + "$Stub");
      Field[] f = cStub.getDeclaredFields();
      for (Field field : f) {
        field.setAccessible(true);
        String k = field.toString().split("\\$Stub\\.")[1];
        if (k.contains("TRANSACTION"))
          codes.put(k, (int) field.get(null));
      }
    } catch (Exception e) {

    }

    return codes;
  }

  //利用反射获取对应接口所有调用的参数类型
  public HashMap<String, List<String>> getBinderCallParameter(String interfaceDescriptor,
                                                              HashMap<String, Integer> codes) {
    HashMap<String, List<String>> ret = new HashMap();

    if (interfaceDescriptor == null)
      return ret;

    try {
      Class<?> cStub = Class
          .forName(interfaceDescriptor + "$Stub$Proxy");
      Method[] m = cStub.getDeclaredMethods();

      for (Method method : m) {
        int func_code = 0;
        List<String> func_parameter = new ArrayList<>();

        method.setAccessible(true);
        String func_name = method.toString().split("\\$Stub\\$Proxy\\.")[1];
        func_parameter.add(func_name);

        for (String key : codes.keySet()) {
          if (func_name.contains(key.substring("TRANSACTION_".length())))
            func_code = codes.get(key);
        }

        if (func_code == 0)
          continue;

        Class<?>[] ParameterTypes = method.getParameterTypes();
        for (int k = 0; k < ParameterTypes.length; k++) {
          func_parameter.add(ParameterTypes[k].toString());
        }

        ret.put(Integer.toString(func_code), func_parameter);
      }
    } catch (Exception e) {
    }

    return ret;
  }


  public void fuzz(String serviceName) throws RemoteException {
    if (serviceName == "suspend_control") {
      return;
    }
    Parcel data = Parcel.obtain();
    Parcel reply = Parcel.obtain();
    File elf = new File(mContext.getFilesDir(), "start.sh");
    int code = 0;
    byte[] payload = null;

    IBinder serviceBinder = getIBinder(serviceName);
    if (serviceBinder == null) {
      return;
    }
    if (serviceBinder.pingBinder()) {
      Log.w(TAG, "-----------------------------" + serviceName + "-----------------------------");
      logu.log("-----------------------------" + serviceName + "-----------------------------");
    }
    String interFaceName = serviceBinder.getInterfaceDescriptor();
    if (interFaceName == null) {
      return;
    }
    HashMap<String, Integer> codes = getBinderCode(interFaceName);

    HashMap<String, List<String>> parame = this.getBinderCallParameter(interFaceName, codes);
    String a = parame.toString();
    if (!codes.isEmpty()) {
      Iterator item = codes.entrySet().iterator();
      while (item.hasNext()) {
        Map.Entry<String, Integer> map = (Map.Entry<String, Integer>) item.next();
        code = map.getValue();
        try {
          data.writeInterfaceToken(interFaceName);

          for (int i = 0; i < 20; i++) {

//                      payload = Radamsa.exec(mContext,new String[]{"sh","-c",elf.getAbsolutePath()});
            data.writeByteArray(new byte[2048]);
            Log.i(TAG, "[FUCK]  " + serviceBinder.getInterfaceDescriptor() + code);
            serviceBinder.transact(code, data, reply, 0);

            if (!serviceBinder.pingBinder() || !serviceBinder.isBinderAlive()) {
              Log.e(TAG, "[FUCKED]  " + serviceBinder.getInterfaceDescriptor() + code);
              logu.log("[FUCKED]  " + serviceBinder.getInterfaceDescriptor() + code);
            }

          }


        } catch (Exception e) {
        }
      }
    } else {

      for (int i = 0; i < 1000; i++) {

        try {

          data.writeInterfaceToken(interFaceName);
//                    payload = Radamsa.exec(mContext,new String[]{"sh","-c",elf.getAbsolutePath()});
          data.writeByteArray(new byte[2048]);
          Log.i(TAG, "[FUCK_SYS]  " + serviceBinder.getInterfaceDescriptor() + i);
          serviceBinder.transact(i, data, reply, 0);
          if (!serviceBinder.pingBinder() || !serviceBinder.isBinderAlive()) {
            Log.e(TAG, "[FUCK_SYSED]  " + serviceBinder.getInterfaceDescriptor() + i);
            logu.log("[FUCK_SYSED]  " + serviceBinder.getInterfaceDescriptor() + i);
          }
        } catch (Exception e) {
        }

      }
    }


  }


  public void fuzz(String serviceName,String interfaceName,int[] code){
    IBinder serviceBinder = getIBinder(serviceName);

  }
}
