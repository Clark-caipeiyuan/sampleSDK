package com.xixi.sdk.sipmsg.params;

import java.util.ArrayList;
import java.util.List;

import com.xixi.sdk.LLSDKUtils;
import com.xixi.sdk.globals.ILiftCmdCacheOpCode;
import com.xixi.sdk.utils.file.IoCompletionListener1;

public class LLLiftCmdCacheNode implements ILiftCmdCacheOpCode{

	int code = opInvalidCode ; 
	
	public int getCode() {
		return code;
	}

	private final List<IoCompletionListener1<Boolean>> opIoList = new ArrayList<IoCompletionListener1<Boolean>>();
//	private final Set<Integer> opFloorSet = new HashSet<Integer>();
	private final List<Integer> opFloorSet = new ArrayList<Integer>();
	
	private boolean isSameRequest(final  LLLiftCmdCacheNode nodeRequest ) {
		return code == nodeRequest.getCode() ; 
	}
	 
	public boolean combinedRequest(final  LLLiftCmdCacheNode nodeRequest ) { 
		if ( !isSameRequest(nodeRequest)) return false ;  
		_combineRequest(nodeRequest.opFloorSet, nodeRequest.opIoList); 
		return true ; 
	}
	
	private void _combineRequest(List<Integer> bitmap, List<IoCompletionListener1<Boolean>> ios) {
		opFloorSet.addAll(bitmap); 
		opIoList.addAll(ios); 
	}

//	public LLLiftCmdCacheNode makeRequest(int code , Set<Integer> bitmap, List<IoCompletionListener1<Boolean>> ios) {
//		this.code = code ; 
//		opFloorSet.addAll(bitmap); 
//		opIoList.addAll(ios);
//		return this ; 
//	}
//	
//	private LLLiftCmdCacheNode makeRequest(int code , String bitMap, List<IoCompletionListener1<Boolean>> ios) {
//		this.code = code ; 
//		LLSDKUtils.string2List(bitMap, opFloorSet);
//		opIoList.addAll(ios);
//		return this ; 
//	}

	public List<IoCompletionListener1<Boolean>> getOpIoList() {
		return new ArrayList<IoCompletionListener1<Boolean>>(opIoList);
	}

	public List<Integer> getOpFloorSet() {
		return new ArrayList<Integer>(opFloorSet);
	}

	public LLLiftCmdCacheNode(int code , String bitMap, List<IoCompletionListener1<Boolean>> ios) {
		this.code = code ; 
		LLSDKUtils.string2List(bitMap, opFloorSet);
		opIoList.addAll(ios); 
	}

	public LLLiftCmdCacheNode(int code , List<Integer> bitMap, List<IoCompletionListener1<Boolean>> ios) {
		this.code = code ; 
		opFloorSet.addAll(bitMap); 
		opIoList.addAll(ios); 
	}
 	
	public static LLLiftCmdCacheNode makeDisableDirectRequestNode(List<Integer> bitMap, List<IoCompletionListener1<Boolean>> ios) { 
		return new LLLiftCmdCacheNode(opDiscardDirectCode , bitMap , ios) ;
	}
	
	public static LLLiftCmdCacheNode makeDisallowRequestNode(List<Integer> bitMap, List<IoCompletionListener1<Boolean>> ios)
	{ 
		return new LLLiftCmdCacheNode(opDisallowCode , bitMap , ios) ;
	}	
}
