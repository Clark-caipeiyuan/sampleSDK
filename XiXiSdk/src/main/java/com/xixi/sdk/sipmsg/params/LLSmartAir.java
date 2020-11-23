package com.xixi.sdk.sipmsg.params;

import com.xixi.sdk.device.IDeviceData;

public class LLSmartAir implements IDeviceData {
	
	private int sign ;
	private int temp ;
	private int model;
	private int  windLevl;  
	public int getWindLevl() {
		return windLevl;
	}
	public void setWindLevl(Integer windLevl) {
		this.windLevl = windLevl;
	}

	public int getSign() {
		return sign;
	}
	public void setSign(Integer sign) {
		this.sign = sign;
	}
	public int getTemp() {
		return temp;
	}
	public void setTemp(Integer temp) {
		this.temp = temp;
	}
	public int getModel() {
		return model;
	}
	public void setModel(int model) {
		this.model = model;
	}
	

	public void setSignAndTemp(int temp , int windLevl , int model ,boolean on_off){
		this.setSign(on_off?1:0);
		this.setTemp(temp);
		this.setModel(model);
		this.setWindLevl(windLevl); 
	}
	public void setSignAndTemp(int temp , int windLevl , int model ,int sign ){
		this.setSign(sign);
		this.setTemp(temp);
		this.setModel(model);
		this.setWindLevl(windLevl); 
	}
	
}
