package com.xixi.sdk.controller;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.longlake.xixisdk.R;
import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.device.LLElevatorController;
import com.xixi.sdk.globals.ILiftCmdCacheOpCode;

import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

public class LLSpeechSynthesizerController
implements IElevatorFloorListener, IElevatorDirectionListener, ISpeechFloorVoice, ILiftCmdCacheOpCode {

	private static LLSpeechSynthesizerController instance;

	private TextToSpeech mTextToSpeech;

	public static synchronized LLSpeechSynthesizerController getInstance() {
		if (instance == null) {
			instance = new LLSpeechSynthesizerController();
		}
		return instance;
	}

	public LLSpeechSynthesizerController() {
		LLElevatorController.getInstance().getDirectionNotifier().registerAsObserver(this, true);
		//LLElevatorController.getInstance().registerAsObserver(this, true);
		mTextToSpeech = new TextToSpeech(LongLakeApplication.getInstance(), new TextToSpeech.OnInitListener() {

			@Override
			public void onInit(int status) {
				if (status == TextToSpeech.SUCCESS) {
					int supported = mTextToSpeech.setLanguage(Locale.CHINESE);
					if ((supported != TextToSpeech.LANG_AVAILABLE)
							&& (supported != TextToSpeech.LANG_COUNTRY_AVAILABLE)) {
						Log.i(LongLakeApplication.DANIEL_TAG, "not support language");
					}
				}

			}
		},"com.iflytek.tts");
	}

	@Override
	public void onElevatorFloorChanged(int showFloor, int currentFloor) {
		/*
		 * if(showFloor < 0){
		 * startSpeak(String.format(LLSDKUtils.getString(R.string.
		 * current_floor_changed), map.get(showFloor))); }else{
		 * startSpeak(String.format(LLSDKUtils.getString(R.string.
		 * current_floor_changed), showFloor)); }
		 */
	}

	public void startSpeak(int str) { 
		startSpeak(LongLakeApplication.getInstance().getResources().getString(str));
	}


	private String lastStatement = "" ; 

	private void _startSpeak(String read) { 
		lastStatement = read ; 
		mTextToSpeech.speak(read, TextToSpeech.QUEUE_FLUSH, null);
	}
	public void startSpeak(String rightNowRead) {
		if ( !TextUtils.equals(rightNowRead , lastStatement) )
		{
			_startSpeak(rightNowRead) ; 
		}
		else { 
			if ( !mTextToSpeech.isSpeaking() ) { 
				_startSpeak(rightNowRead) ; 
			}
		}
	}

	Set<Integer> _floors_num = new HashSet<Integer>();

	@Override
	public void onSpeechFloor(List<Integer> floors, int op) {
		if (floors.size() != 0) {
			if (op == opAllAllowCode) {
				startSpeak(LLSDKUtils.getString(R.string.all_allow_floors));
			} else if (op == opDirectCode) {
				_floors_num.addAll(floors);
				speakPressDirectFloor(floors.get(0), LLSDKUtils.getString(
						_floors_num.size() == 1 ? R.string.press_direct_floor_fisrt : R.string.press_direct_floors));

				_floors_num.clear();
			}
		}
	}

	public void speakArriveFloor(int realFloor, String text) {
		if (realFloor < 0) {
			startSpeak(String.format(text, map.get(realFloor)));
		} else {
			startSpeak(String.format(text, realFloor));
		}
	}

	private void speakPressDirectFloors(int floor, String text) {
		if (floor < 0) {
			startSpeak(String.format(text, map.get(floor)));
		} else {
			startSpeak(String.format(text, floor));
		}
	}

	private void speakPressDirectFloor(int floor, String text) {
		if (floor < 0) {
			startSpeak(String.format(text, map.get(floor)));
		} else {
			startSpeak(String.format(text, floor));
		}
	}

	private static final Map<Integer, String> map;
	static {
		map = new HashMap<Integer, String>();
		map.put(-1, "B1");
		map.put(-2, "B2");
		map.put(-3, "B3");
		map.put(-4, "B4");
		map.put(-5, "B5");
	}

	@Override
	public void onElevatorDirectionChanged(int direction) {
		startSpeak(direction == UP_STATUS ? LLSDKUtils.getString(R.string.up_direction)
				: LLSDKUtils.getString(R.string.down_direction));

	}
}
