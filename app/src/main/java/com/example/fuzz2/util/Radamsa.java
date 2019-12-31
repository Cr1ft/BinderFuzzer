package com.example.fuzz2.util;

import android.content.Context;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedReader;
import java.io.UnsupportedEncodingException;

public class Radamsa {

  public Radamsa() {
  }

  public static byte[] exec(Context context, String[] cmd) {
    Process pro = null;
    byte[] buff = new byte[1024];

    BufferedReader error = null;
    StringBuilder str = new StringBuilder();

    byte[] re = null;
    ByteArrayOutputStream b = null;

    try {
      pro = (Process) Runtime.getRuntime().exec(cmd, null, context.getFilesDir());

      error = new BufferedReader(new InputStreamReader(pro.getErrorStream(), "UTF-8"));
      String line = null;
      pro.waitFor();
      b = new ByteArrayOutputStream(1024);
      int len = pro.getInputStream().read(buff);

      while (len > 0) {
        b.write(buff, 0, len);
        b.flush();
        len = pro.getInputStream().read(buff);
      }

      while ((line = error.readLine()) != null) {
        str.append(line);
      }

      re = b.toByteArray();


    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      closeStream(b);
      if (pro != null) {
        pro.destroy();
      }
    }
    return re;
  }

  private static void closeStream(Closeable stream) {
    if (stream != null) {
      try {
        stream.close();
      } catch (Exception e) {
        // nothing
      }
    }
  }

}
