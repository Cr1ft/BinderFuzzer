package com.example.fuzz2;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fuzz2.util.BinderInfo;
import com.example.fuzz2.util.FuzzThread;
import com.example.fuzz2.util.FuzzUtil;
import com.example.fuzz2.util.LogUtils;
import com.example.fuzz2.util.Radamsa;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {
    private Button button_start;
    private Button buttonTest;
    private static final String TAG = "SECNEO";
    private BinderInfo binderInfo;
    private String[] services;
    private String[] allInterfaceName;
    private LogUtils logu;
    private EditText ed;
    private TextView tv;
    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final FuzzUtil fuzzu = FuzzUtil.getInstance(MainActivity.this);
        fuzzu.copyFile2EXe("radamsa","radamsa");
        fuzzu.copyFile2EXe("start.sh","start.sh");

        button_start = (Button) findViewById(R.id.button);
        buttonTest = (Button) findViewById(R.id.button2);
        //test
        ed = findViewById(R.id.editText);
        tv = findViewById(R.id.textView);



        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binderInfo = new BinderInfo(MainActivity.this);
                logu = binderInfo.getLogu();
                services = binderInfo.getServices();
                Log.d(TAG, "FUZZ START ..........................");
                logu.log("FUZZ START ...............................");

                int thNum = 10;
                int ServiceNum = services.length;
                int mod = ServiceNum % thNum;
                int n  = (int) ServiceNum / thNum;
                int start = 0;
                int end =0;
                Thread T = null;
                String[] tmp;
                FuzzThread[] a = new FuzzThread[thNum];
                FuzzThread th =null;
                for(int i = 1 ; i <= thNum ; i++){
                    if(mod == 0 ){
                        tmp = fuzzu.splitStringArray(services,start,start+n);
                        th=new FuzzThread(MainActivity.this,tmp,i);
                        a[i-1]=th;
                        start = start+n;
                    }else{

                        if(i==thNum){
                            tmp = fuzzu.splitStringArray(services,start,start+mod);
                            th=new FuzzThread(MainActivity.this,tmp,i);
                            a[i-1]=th;
                            start = start+n;
                            break;
                        }
                        tmp = fuzzu.splitStringArray(services,start,start+n);
                        th=new FuzzThread(MainActivity.this,tmp,i);
                        a[i-1]=th;
                        start = start+n;
                    }


                }

                for(FuzzThread t : a){
                    T = new Thread(t);
                    T.start();
                }

            }
        });


        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String cmd = ed.getText().toString();
                    BufferedReader o = null;
                    BufferedReader e = null;
                    StringBuilder s = new StringBuilder();
                    Process p = null;
                    if(cmd.isEmpty()){
                        File ff = new File("/data/local/tmp/","radamsa");
                         p = (Process) Runtime.getRuntime().exec(new String[]{"sh","-c","./start.sh"},null,getFilesDir());
                    }else{

                        p = (Process) Runtime.getRuntime().exec(cmd,null,getFilesDir());
                    }

                    o = new BufferedReader(new InputStreamReader(p.getInputStream(),"UTF-8"));
                    e = new BufferedReader(new InputStreamReader(p.getErrorStream(),"UTF-8"));

                    String line =null;

                    while((line = o.readLine())!=null){
                        s.append(line);
                    }
                    while((line = e.readLine())!=null){
                        s.append(line);
                    }
                    o.close();
                    e.close();
                    Toast.makeText(MainActivity.this,s,Toast.LENGTH_LONG).show();


                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });



    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
//    public native String stringFromJNI();
}
