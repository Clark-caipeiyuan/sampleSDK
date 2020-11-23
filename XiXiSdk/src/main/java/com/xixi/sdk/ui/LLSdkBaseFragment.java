
package com.xixi.sdk.ui;

import java.io.Serializable;
import java.util.List;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.app.LongLakeApplication;
import com.xixi.sdk.controller.LLAppStatistics;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public abstract class LLSdkBaseFragment extends Fragment {

	public final static String ACTIVITY_PARAM = "ACTIVITY_PARAM";

	protected abstract int getLayoutId();

	protected String params[];
	
	List<Serializable> serialParams ; 
	
	protected List<Serializable> getSerialParams() { 
		return serialParams; 
	}
	private boolean umengLogSwitch = true;
	public boolean isUmengLogSwitch() {
		return umengLogSwitch;
	}

	public void setUmengLogSwitch(boolean umengLogSwitch) {
		this.umengLogSwitch = umengLogSwitch;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(isUmengLogSwitch()){
			LLAppStatistics.getInstance().statisticsPageStart();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(isUmengLogSwitch()){
			LLAppStatistics.getInstance().statisticsPageEnd();
		}
	}

	public void onLongLakePause() {
	}

	public void onLongLakeResume() {
	}

	public void onLongLakeRefresh() {

	}

	public Context getContext() {
		return LongLakeApplication.getInstance();
	}

	protected String _getString(int id) {
		return getContext().getString(id);
	}

	protected View mainView;

	public View getMainView() {
		return mainView;
	}

	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if (params != null) {
			outState.putStringArray(ACTIVITY_PARAM, params);
			
		}
		if ( serialParams != null ) { 
			LLSDKUtils.putSerializable(serialParams.toArray(new Serializable[0]), outState);
		}  
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mainView = inflater.inflate(getLayoutId(), null);
		if (savedInstanceState == null) {
			savedInstanceState = this.getArguments();
		}
		
		if (savedInstanceState != null) {
			params = savedInstanceState.getStringArray(ACTIVITY_PARAM);
			serialParams = LLSDKUtils.getSerializable(savedInstanceState) ;
		}
		return mainView;
	}

	public void toast(String tag) {
		Toast.makeText(getContext(), tag, Toast.LENGTH_SHORT).show();
	}

	public void toast(int rid) {
		Toast.makeText(getContext(), _getString(rid), Toast.LENGTH_SHORT).show();
	}

}
