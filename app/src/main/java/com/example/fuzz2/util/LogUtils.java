package com.example.fuzz2.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class LogUtils {
    private static LogUtils instance = null;
    private FileOutputStream fileOutputStream;


    private LogUtils(String path) {
        try {
            fileOutputStream = new FileOutputStream(new File(path));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static LogUtils getInstance(String path) {
        if (instance == null) {
            instance = new LogUtils(path);
        }
        return instance;
    }

    public void log(String message) {
        try {
            fileOutputStream.write(message.getBytes());
            fileOutputStream.write("\n".getBytes());
            fileOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void close() {
        try {
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
