package com.xixi.sdk.sipmsg.params;
import java.util.ArrayList;
import java.util.TreeSet;

public class LiftStartPTreeMap extends LiftTargetTreeMap {

	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -997473638110938599L;

	public void addFloor(LLLiftCmdBean key){
		TreeSet<LLLiftCmdBean> value = get(key.getLogicalLocationOfFloor());
		if(value == null){
			value = mm.popBuffer();
			super.put(key.getLogicalLocationOfFloor(), value) ;
		}
		value.add(key);
	} 
	 
	
	
}
