package com.example.fuzz2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuzz2.Service.FuzzService;
import com.example.fuzz2.util.FuzzUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
  private Button button_start;
  private Button button_stop;
  private Button button_dest;
  private EditText ed_serviceName;
  private EditText ed_interfacename;
  private EditText ed_code;
  private Intent intent;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    //每次启动释放脚本文件
    final FuzzUtil fuzzu = FuzzUtil.getInstance(MainActivity.this);
    fuzzu.copyFile2EXe("radamsa", "radamsa");
    fuzzu.copyFile2EXe("start.sh", "start.sh");
    //初始化变量
    initVar();
    //设置点击监听
    button_dest.setOnClickListener(this);
    button_stop.setOnClickListener(this);
    button_start.setOnClickListener(this);


  }


  private void initVar() {
    intent = new Intent(this, FuzzService.class);
    button_start = findViewById(R.id.button);
    button_stop = findViewById(R.id.button_stop);
    button_dest= findViewById(R.id.button2);
    ed_serviceName = findViewById(R.id.editText_service_name);
    ed_code = findViewById(R.id.editText3_code);
    ed_interfacename = findViewById(R.id.editText2_interfacename);

  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.button:
        startService(intent);
        break;
      case R.id.button2:
        break;
      case R.id.button_stop:
        stopService(intent);
        break;
      default:
        break;
    }
  }

  /**
   * A native method that is implemented by the 'native-lib' native library,
   * which is packaged with this application.
   */
//    public native String stringFromJNI();
}
