package com.example.voice;

import java.util.ArrayList;

import android.os.Bundle;


//用来限制所有的需要调用讯飞语音识别后，针对结果进行各自自定义的处理
public interface IVoiceRecognizeWatcher {
	//在语音识别后，对结果进行各自不同的处理
 /*void onResults(ArrayList<RecognizerResult> results ,boolean flag);
 //语音识别结束时需要进行的处理
 void onEnd(SpeechError errorStr);*/
	 /**
     * 准备好可以说话
     * @param param
     */
    void onReadyForSpeach(Object param);
    /**
     * 录音结果出来
     * type:1 讯飞识别出来的结果 2 百度识别出来的结果
     * @param result
     */
    void onResults(ArrayList<String> result, int type);
    /**
     * 录音结束
     * @param param
     */
    void onEndOfRecord(Object param);
    /**
     * 开始录音
     */
    void onBeginRecord();

    /**
     * 识别出错
     * @param b 错误信息
     */
    void onError(Bundle b);

    /**
     * 音量变化回调
     * @param volume
     */
    void onVolumeChange(int volume);

}
