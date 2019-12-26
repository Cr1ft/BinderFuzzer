package com.example.fuzz2.util;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class FuzzUtil {
    private Context mContext;
    private static FuzzUtil instance = null;
    public static FuzzUtil getInstance(Context mContext) {
        if (instance == null) {
            instance = new FuzzUtil(mContext);
        }
        return instance;
    }

    public FuzzUtil(Context context){
        mContext = context;
    }

    public void copyFile2EXe(String srcFileName,String destFileName){
        try {

            File elf = new File(mContext.getFilesDir(),destFileName);
            if(!elf.exists()){
                InputStream input = mContext.getAssets().open(srcFileName);
                FileOutputStream f = new FileOutputStream(elf.getAbsoluteFile());
                byte[] buff = new byte[1024];
                int len = input.read(buff);
                while (len > 0){
                    f.write(buff, 0, len);
                    f.flush();
                    len = input.read(buff);
                }
                f.flush();
                f.close();
                input.close();
                Radamsa.exec(mContext,new String[]{"chmod","777",elf.getAbsolutePath()});
            }


        } catch (IOException e) {
            e.printStackTrace();
        }


    }



    public String[] splitStringArray(String[] sArray,int start,int end){
        List<String> buff = new ArrayList<>();
        for(int i = start; i < end; i++) {
            buff.add(sArray[i]);
        }
        return buff.toArray(new String[buff.size()]);
    }
}
