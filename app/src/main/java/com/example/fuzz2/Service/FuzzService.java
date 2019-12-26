package com.example.fuzz2.Service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class FuzzService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
