package com.example.voice;

import java.util.ArrayList;
import java.util.Calendar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.Time;
import android.widget.Toast;

import com.example.xiaomaolv.naoturing.R;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;


public class XunFeiRecognizer extends VoiceRecognizerBase {

	// Context mContext;
	private SharedPreferences mSharedPreferences;
	SpeechRecognizer mIat;
	int ret = 0;// 函数调用返回值
	private StringBuilder xunfeiSB;
	private Toast mToast;
	private boolean init_compelet = false;
	boolean needRecognizeDirectly = false;
	private static final String time_out = "8000";
	private static final String VAD_BOS = "6500";// 判断没有说话的时间点，最大支持10秒
	private static final String VAD_EOS = "100";// 判断没有说话的时间点，最大支持10秒
	private static final String TAG = XunFeiRecognizer.class.getName();
	/** 没有说话的计数变量 */
	private int recognizeNoDataCount = 0;

	private String[] errorTipsSet;
	private int randomIndex = -1;
	private long start_time_point=0;
	private int retryCount=0;
	public static final String PREFER_NAME = "com.iflytek.setting";

	public XunFeiRecognizer(Context mContext,
			IVoiceRecognizeWatcher recognizeWatcher, Handler mHandler) {
		super(mContext, recognizeWatcher, mHandler);
		errorTipsSet = mContext.getResources().getStringArray(
				R.array.recognize_error_tips);
		initXunfei();
		xunfeiSB = new StringBuilder();
	}

	/**
	 * 初始化讯飞组件
	 * 	 */
	private void initXunfei() {
		SpeechUtility.createUtility(mContext.getApplicationContext(), "appid="
				+ mContext.getString(R.string.xunfei_appid));
		// 初始化识别对象
		if (SpeechRecognizer.getRecognizer() != null) {
			mIat = SpeechRecognizer.getRecognizer();
			init_compelet = true;
		} else
			mIat = SpeechRecognizer.createRecognizer(mContext, mInitListener);
		mSharedPreferences = mContext.getSharedPreferences(
				PREFER_NAME, Activity.MODE_PRIVATE);

	}

	@Override
	public void startRecognize() {

		if (init_compelet) {
			needRecognizeDirectly = false;
			setParam();

			mHandler.postDelayed(new Runnable() {

				@Override
				public void run() {

					if (!isCancled && !mIat.isListening()) {
						ret = mIat.startListening(recognizerListener);
						if (ret != ErrorCode.SUCCESS) {
							showTip("听写失败,错误码：" + ret);
						} else {
						}
					}
				}
			}, 0);

		} else {
			needRecognizeDirectly = true;
		}

	}

	@Override
	public void stopRecognize() {
		if (mIat.isListening()) {
			mIat.stopListening();
		}
		clearWatchers();
	}

	@Override
	public void cancelRecognize() {
		if (mIat != null) {
			mIat.cancel();

		}
	}

	/**
	 * Toast展示提示信息
	 * 
	 * @param str
	 */
	private void showTip(final String str) {
		((Activity) mContext).runOnUiThread(new Runnable() {
			@Override
			public void run() {
				mToast.setMargin(0, (float) 0.07);
				mToast.setText(str);
				mToast.show();
			}
		});
	}

	/**
	 * 参数设置
	 *
	 * @return
	 */
	@SuppressLint("SdCardPath")
	public void setParam() {
		String lag = mSharedPreferences.getString("iat_language_preference",
				"mandarin");
		if (lag.equals("en_us")) {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
		} else {
			// 设置语言
			mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			// 设置语言区域
			mIat.setParameter(SpeechConstant.ACCENT, lag);
		}
		mIat.setParameter(SpeechConstant.NET_TIMEOUT, time_out);
		// 设置语音前端点
		mIat.setParameter(SpeechConstant.VAD_BOS,
				mSharedPreferences.getString("iat_vadbos_preference", VAD_BOS));
		// 设置语音后端点
		mIat.setParameter(SpeechConstant.VAD_EOS,
				mSharedPreferences.getString("iat_vadeos_preference", VAD_EOS));
		// 设置标点符号
		mIat.setParameter(SpeechConstant.ASR_PTT,
				mSharedPreferences.getString("iat_punc_preference", "1"));
		// 设置音频保存路径
		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
				"/sdcard/iflytek/wavaudio.pcm");
	}

	private String delUserlessSuffix(String strRegResult) {
		// 对识别结果做字符串处理
		if (strRegResult.endsWith("。")) // 删除尾部的句号
			strRegResult = strRegResult.substring(0, strRegResult.length() - 1);
		if (strRegResult.startsWith("哦")) // 删除句首的"哦"
			strRegResult = strRegResult.substring(1, strRegResult.length());
		if (strRegResult.startsWith("，") || strRegResult.startsWith("。"))// 删除句首的"，"和"。"
			strRegResult = strRegResult.substring(1, strRegResult.length());

		return strRegResult;
	}

	public void clearWatchers() {
		if (mIVoiceRecognizeWatcher != null)
			mIVoiceRecognizeWatcher = null;
	}

	public IVoiceRecognizeWatcher getmIVoiceRecognizeWatcher() {
		return mIVoiceRecognizeWatcher;
	}

	public void setmIVoiceRecognizeWatcher(
			IVoiceRecognizeWatcher mIVoiceRecognizeWatcher) {
		this.mIVoiceRecognizeWatcher = mIVoiceRecognizeWatcher;
	}

	public int getRecognizeTime() {
		return recognizeNoDataCount;
	}

	public void setRecognizeTime(int recognizeTime) {
		this.recognizeNoDataCount = recognizeTime;
	}

	/**
	 * 初始化监听器。
	 */
	private InitListener mInitListener = new InitListener() {

		@Override
		public void onInit(int code) {
			if (code == ErrorCode.SUCCESS) {
				init_compelet = true;
				if (needRecognizeDirectly) {
					startRecognize();
				}
			}
		}
	};
	/**
	 * 讯飞听写监听器。
	 */
	private RecognizerListener recognizerListener = new RecognizerListener() {

		@Override
		public void onBeginOfSpeech() {

			start_time_point=Calendar.getInstance().getTimeInMillis();

			if (mIVoiceRecognizeWatcher != null) {
				if (getState() != begin_record) {
					setState(begin_record);
					mIVoiceRecognizeWatcher.onBeginRecord();

				}
			}
		}

		@Override
		public void onError(SpeechError error) {

			int code = error.getErrorCode();
			setState(error_record);
			Bundle b = null;

			switch (error.getErrorCode()) {
			case ErrorCode.ERROR_NET_EXPECTION:
			case ErrorCode.ERROR_NO_NETWORK:
			case ErrorCode.ERROR_NETWORK_TIMEOUT:
			case ErrorCode.MSP_ERROR_TIME_OUT:
				b = createErrorInfo(getRecognizeTime(), 1, 1,
						mContext.getString(R.string.tip_net_disconnet), true);
				break;
			case ErrorCode.ERROR_ENGINE_BUSY:
			case ErrorCode.ERROR_ENGINE_CALL_FAIL:
			case ErrorCode.ERROR_ENGINE_INIT_FAIL:
			case ErrorCode.ERROR_ENGINE_NOT_SUPPORTED:
				b = createErrorInfo(
						getRecognizeTime(),
						1,
						1,
						mContext.getString(R.string.tip_recognize_error_engine),
						true);
				break;
			case ErrorCode.MSP_ERROR_NO_DATA:
				long end_time_point=Calendar.getInstance().getTimeInMillis();
				
				if(end_time_point-start_time_point<6000){
					
					if(retryCount<2){
						retryCount++;
						startRecognize();
						return;
					}
					else
						retryCount=0;
				}
				if (getRecognizeTime() < 2) {
					int tempIndex = (int) (Math.random() * (errorTipsSet.length - 1));
					while (randomIndex == tempIndex) {
						tempIndex = (int) (Math.random() * (errorTipsSet.length - 1));
					}
					randomIndex = tempIndex;
					b = createErrorInfo(getRecognizeTime(), 1, 2,
							errorTipsSet[randomIndex], false);
					setRecognizeTime(getRecognizeTime() + 1);
				} else {
					b = createErrorInfo(getRecognizeTime(), 1, 2,
							mContext.getString(R.string.tip_nodata_exit), true);
					setRecognizeTime(0);
				}

				break;

			default:
				b = createErrorInfo(getRecognizeTime(), 1, 0,
						mContext.getString(R.string.tip_volume_toosmall), false);
				break;
			}
			if (mIVoiceRecognizeWatcher != null) {
				mIVoiceRecognizeWatcher.onError(b);
			}
		}

		@Override
		public void onEndOfSpeech() {

			if (mIVoiceRecognizeWatcher != null) {
				mIVoiceRecognizeWatcher.onEndOfRecord(null);
				setState(end_record);
			}
		}

		@Override
		public void onResult(RecognizerResult results, boolean isLast) {

			setRecognizeTime(0);
			String text = JsonParser.parseIatResult(results.getResultString());
			if (mContext != null) {

				xunfeiSB.append(text);
				String strRegResult = xunfeiSB.toString().trim();

				strRegResult = delUserlessSuffix(strRegResult);
				if (isLast) {
					if (result_array != null)
						result_array.clear();
					else
						result_array = new ArrayList<String>();
					result_array.add(strRegResult);
					if (mIVoiceRecognizeWatcher != null) {
						setState(on_result);

						mIVoiceRecognizeWatcher.onResults(result_array, 1);
					}
					xunfeiSB.delete(0, xunfeiSB.length());
				}
			}
			setState(on_result);

		}

		@Override
		public void onVolumeChanged(int volume) {
			if (mIVoiceRecognizeWatcher != null) {
				setState(recordding);
				mIVoiceRecognizeWatcher.onVolumeChange(volume);
			}
		}

		@Override
		public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {

		}

	};

}
