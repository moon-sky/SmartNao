package com.example.voice;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;


public abstract class VoiceRecognizerBase {
	protected boolean isCancled = false;
	
	int state = -1;// -1 未开始 0 开始录音 1 录音中 2 录音结束 3 错误 4获得结果
	public static final int un_begin=-1;
	public static final int begin_record=0;
	public static final int recordding=1;
	public static final int end_record=2;
	public static final int error_record=3;
	public static final int on_result=4;
	protected Toast mToast;
	protected Handler mHandler;
	protected Context mContext;
	protected IVoiceRecognizeWatcher mIVoiceRecognizeWatcher;
	public IVoiceRecognizeWatcher getmIVoiceRecognizeWatcher() {
		return mIVoiceRecognizeWatcher;
	}
	public void setmIVoiceRecognizeWatcher(
			IVoiceRecognizeWatcher mIVoiceRecognizeWatcher) {
		this.mIVoiceRecognizeWatcher = mIVoiceRecognizeWatcher;
	}
	protected ArrayList<String> result_array;
	public VoiceRecognizerBase(Context context,IVoiceRecognizeWatcher recognizeWatcher,Handler mHandler) {
		this.mHandler=mHandler;
		this.mIVoiceRecognizeWatcher=recognizeWatcher;
		this.mContext = context;
		result_array=new ArrayList<String>();
	}
	abstract void startRecognize();
	abstract void stopRecognize();
	abstract void cancelRecognize();
	
	public boolean isCancled() {
		return isCancled;
	}
	public void setCanceld(boolean isCancled) {
		this.isCancled = isCancled;
	}
//	public boolean isCancel() {
//		return isCancel;
//	}
//	public void setCancel(boolean isCancel) {
//		this.isCancel = isCancel;
//	}
	public void setState(int state){
		this.state=state;
	}
	public int getState(){
		return state;
	}
	/**
	 * 创建错误信息
	 * @param recognizerType 识别引擎的类型 0： 百度 1：讯飞
	 * @param errorType 错误类型 0：默认  1：网络不稳定  2：没有听到说话  （全语音场景下前两者要显示气泡）
	 * @param information 错误信息
	 * @return
	 */
	protected Bundle createErrorInfo(int errorcount,int recognizerType,int errorType,String information,boolean isBye){
		Bundle b = new Bundle();
		b.putInt("recognizerType", recognizerType);
		b.putInt("errorType", errorType);
		b.putString("information", information);
		b.putBoolean("isBye", isBye);
		b.putInt("errorcount", errorcount);
		return b;
	}
}
