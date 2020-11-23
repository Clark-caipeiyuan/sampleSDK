package com.xixi.sdk.utils.file;

import java.util.HashSet;
import java.util.Set;

import com.xixi.sdk.parser.LLGsonUtils;

public class LLDoorSafeUserUtils {
	private final String FILENAME = "USER_DATA_FILE"; 
	public String getFILENAME() {
		return FILENAME;
	}

	final Set<String> subscribers = new HashSet<String>(); 
//	private  static LLDoorSafeUserUtils instance = null;
//	public static LLDoorSafeUserUtils getInstance() {
//		if (instance == null) {
//			instance = new LLDoorSafeUserUtils();
//		}
//		return instance;
//	}

	public void getAllUserDataOverFile(final IoCompletionListener1<String> callback) {
		LLFileUtils.getInstance().readFromFile(FILENAME, new IoCompletionListener1<String>() {
			@Override
			public void onFinish(String data, Object context) {
				// TODO Auto-generated method stub
				callback.onFinish(data, null);
			}
		});
	}
	public String getAllUserDataOverCatch(){
		return LLGsonUtils.getInstance().toJson(subscribers);
	}
	public void writeUsersToFile(Set<String> users){
		String userStr = LLGsonUtils.getInstance().toJson(users);
		LLFileUtils.getInstance().writeToFile(FILENAME, userStr);
		
	}
	public void operateUserData(final String user, final boolean isAdd) {
		// final String dataStr = LLGsonUtils.getInstance().toJson(userData);
		boolean contained = subscribers.contains(user)  ; 
		if ( isAdd && !contained) { 
			subscribers.add(user) ;
		}else if ( !isAdd && contained ) { 
			subscribers.remove(user) ; 
		}else{
			return ;
		}
		writeUsersToFile(subscribers);
	} 
	
	public int sizeOfSubscribers() {
		return subscribers.size() ;
	}
	public Set<String> getSubscribers() {
		return subscribers;
	}

	public boolean isContain(String user){ 
		return subscribers.contains(user)  ; 
	} 
	
	public void setSubscribers(String subs,boolean isWriteToFile){ 
		@SuppressWarnings("unchecked")
		Set<String> _sub = (Set<String>)LLGsonUtils.fromJson(subs, (new HashSet<String>()).getClass());
		if ( _sub != null ) { 
			subscribers.addAll(_sub) ;
			if(isWriteToFile){
				writeUsersToFile(subscribers);
			}
		}
	}
	
	protected LLDoorSafeUserUtils() { 
	}
}
