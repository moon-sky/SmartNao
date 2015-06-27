package com.example.net;

import android.os.AsyncTask;
import android.os.Handler;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by xiaomaolv on 2015/6/27.
 */
public class MyHttpTask extends AsyncTask<String,Void,String> {
Handler mHandler;
    int msg_what_success;
    int msg_what_fail;
    public MyHttpTask(Handler handler,int msg_success,int msg_error){
        this.mHandler=handler;
        this.msg_what_success=msg_success;
        this.msg_what_fail=msg_error;
    }
    @Override
    protected String doInBackground(String... url) {

        try {
            return   urlConnect(url[0]);
        } catch (IOException e) {
            e.printStackTrace();
            return "URL CONNECT ERROR";
        }
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }
    private String urlConnect(String myUrl) throws IOException {
        InputStream inputStream=null;
        String content = null;
        try {
            URL url=new URL(myUrl);
           HttpURLConnection connection= (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();
            int response=connection.getResponseCode();
            if (response==200){
                inputStream=connection.getInputStream();
                content=readIt(inputStream, 1024);
            }

        }finally {
            inputStream.close();
        }
        return content;
    }
    // Reads an InputStream and converts it to a String.
    public String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
