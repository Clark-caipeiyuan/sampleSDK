package com.xixi.sdk.observer;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLNotifier;
import com.xixi.sdk.utils.file.IoCompletionListener1;
import com.xixi.sdk.utils.queue.LLCalculateGravityQueue;
import com.xixi.sdk.utils.queue.LLSmoothRawDataGravityQueue;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;

public class KnockDetectObserver extends LLNotifier<IoCompletionListener1<String>>implements SensorEventListener {

	private static KnockDetectObserver instance;

	public static synchronized KnockDetectObserver getInstance() {
		if (instance == null) {
			instance = new KnockDetectObserver();
		}
		return instance;
	}
	protected boolean dispatchedInMainThread() { 
		return true ; 
	}
	public interface KnowDetectConfigureAdapter {
		public Context getContext();
//		public float getBeatMaxNumber();
		public long getBeatDuration();
		public float getBeatingThreshold();
	};

	KnowDetectConfigureAdapter configurationAdapter  = LLBeatingDetectConfigureAdapter.getInstance() ;

	public KnowDetectConfigureAdapter getConfigurationAdapter() {
		return configurationAdapter;
	}

	public KnockDetectObserver setConfigurationAdapter(KnowDetectConfigureAdapter configurationAdapter) {
		this.configurationAdapter = configurationAdapter;
		return this;
	}

	private SensorManager sensorManager;
	private Sensor accelerationSensor; 
	private int FrequentType = SensorManager.SENSOR_DELAY_UI; 
	long beatTimestamp = - configurationAdapter.getBeatDuration() - 1 ;  
	long beatWarningTime = - configurationAdapter.getBeatDuration() - 1 ;
	int beatWarningFrequency = 0;
	void checkIfBeatingHarshly(double th, long timestamp) {
		if (th > configurationAdapter.getBeatingThreshold()) {
			if (timestamp > beatTimestamp + configurationAdapter.getBeatDuration() ) {
				beatTimestamp = timestamp;
				this.notifyOb(null);
			}
		}
	}

	public void init() {
		sensorManager = (SensorManager) LongLakeApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
		accelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		sensorManager.registerListener(this, accelerationSensor, FrequentType);

		HandlerThread dataProcessor = new HandlerThread("STL");
		dataProcessor.start();
		dataHandler = new Handler(dataProcessor.getLooper());
	}

	Handler dataHandler;
	CurrentVal currentData;

	final LLSmoothRawDataGravityQueue smoothDataQueue = new LLSmoothRawDataGravityQueue();
	final LLCalculateGravityQueue calculateQueue = new LLCalculateGravityQueue();
	private  void readLineFile(String filename){  
		HandlerThread dataProcessor = new HandlerThread("STL");
		dataProcessor.start();
		dataHandler = new Handler(dataProcessor.getLooper());
        try { 
        	String[] data = new String[]{};
            FileInputStream in = new FileInputStream(filename);  
            InputStreamReader inReader = new InputStreamReader(in, "UTF-8");  
            BufferedReader bufReader = new BufferedReader(inReader);  
            String line = null;  
            int i = 1;   
            while((line = bufReader.readLine()) != null){
            	data = line.split(",");
            	 final  long timestamp = Long.parseLong(data[3].trim());
         		final float xyz[] = new float[] { Float.parseFloat(data[0]), Float.parseFloat(data[1]), Float.parseFloat(data[2])};
         		dataHandler.post(new Runnable() {
         			@Override
         			public void run() {
         				smoothDataQueue.pushData(Math.abs(xyz[0]), Math.abs(xyz[1]), Math.abs(xyz[2]), timestamp);
         				currentData = smoothDataQueue.getAverage();
         				calculateQueue.calculateChangeRate(currentData);
         				checkIfBeatingHarshly(calculateQueue.getChangeRate(), calculateQueue.getChangeRateTime());
//         				write(currentData.getMod(), calculateQueue.getChangeRate());
         				
         			}
         		});
            	
            }  
            bufReader.close();  
            inReader.close();  
            in.close();
        } catch (Exception e) {  
            e.printStackTrace();  
        }
    } 
	
	@Override
	public void onSensorChanged(final SensorEvent arg0) {
		
		final long timestamp = System.currentTimeMillis();
		final float xyz[] = new float[] { arg0.values[0],  arg0.values[1],  arg0.values[2]};
		dataHandler.post(new Runnable() {
			@Override
			public void run() {
				smoothDataQueue.pushData(Math.abs(xyz[0]), Math.abs(xyz[1]), Math.abs(xyz[2]), timestamp);
				currentData = smoothDataQueue.getAverage();
				calculateQueue.calculateChangeRate(currentData);
				checkIfBeatingHarshly(calculateQueue.getChangeRate(), calculateQueue.getChangeRateTime());
			}
		});
	}

	
	StringBuilder sbBuilder = new StringBuilder();

	private void write(double d, double changeRate) {
		try {
			File file = new File(Environment.getExternalStorageDirectory(), "1.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
			String info = "changeRate=    " + changeRate + "        mod=         " + d;
			bw.write(info);
			bw.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {

	}

	public static class CurrentVal {
		private float currentXVal = 0;
		private float currentYVal = 0;
		private float currentZVal = 0;
		private double mod;
		private long currentTime;

		public CurrentVal() { 
			this(0) ; 
		} 
		public CurrentVal(double _mod ) { 
			mod = _mod ; 
		}
		public long getCurrentTime() {
			return currentTime;
		}

		public void setCurrentTime(long currentTime) {
			this.currentTime = currentTime;
		}

		public double getMod() {
			return mod;
		}

		public void setMod(double mod) {
			this.mod = mod;
		}

		public void setCurrentVal(float currentXVal, float currentYVal, float currentZVal, long currentTime) {
			this.currentXVal = currentXVal;
			this.currentYVal = currentYVal;
			this.currentZVal = currentZVal;
			this.currentTime = currentTime;
		}

		public float getCurrentXVal() {
			return currentXVal;
		}

		public void setCurrentXVal(float currentXVal) {
			this.currentXVal = currentXVal;
		}

		public float getCurrentYVal() {
			return currentYVal;
		}

		public void setCurrentYVal(float currentYVal) {
			this.currentYVal = currentYVal;
		}

		public float getCurrentZVal() {
			return currentZVal;
		}

		public void setCurrentZVal(float currentZVal) {
			this.currentZVal = currentZVal;
		}

	}

	@Override
	protected void invoke(IoCompletionListener1<String> t, Object[] o1) {
		t.onFinish("BEATING_NOTIFICATION", null);
	}

}