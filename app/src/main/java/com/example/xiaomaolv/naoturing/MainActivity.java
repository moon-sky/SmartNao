package com.example.xiaomaolv.naoturing;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.voice.IVoiceRecognizeWatcher;
import com.example.voice.XunFeiRecognizer;


import java.util.ArrayList;
import java.util.logging.LogRecord;


public class MainActivity extends ActionBarActivity implements IVoiceRecognizeWatcher{

    XunFeiRecognizer recognizer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recognizer=new XunFeiRecognizer(this,this,handler);
        findViewById(R.id.btn_recognize).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recognizer.startRecognize();
            }
        });
    }
Handler handler=new Handler() {
    @Override
    public void handleMessage(Message msg) {
        Toast.makeText(MainActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
    }
};
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public void onReadyForSpeach(Object param) {

    }

    @Override
    public void onResults(ArrayList<String> result, int type) {
    handler.obtainMessage(1,result.get(0)).sendToTarget();
    }

    @Override
    public void onEndOfRecord(Object param) {

    }

    @Override
    public void onBeginRecord() {

    }

    @Override
    public void onError(Bundle b) {

    }

    @Override
    public void onVolumeChange(int volume) {

    }
}
