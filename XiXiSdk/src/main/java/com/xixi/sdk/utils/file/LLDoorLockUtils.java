package com.xixi.sdk.utils.file;

public class LLDoorLockUtils {
	final String FILENAME = "LOCK_PWD";
	private String password = "111111";

	public String getPassword() {
		return password;

	}

	public void setPassword(String pwd) {
		password = pwd;
	}

	public void getLockPwdOverFile(final IoCompletionListener1<String> callback) {
		LLFileUtils.getInstance().readFromFile(FILENAME, new IoCompletionListener1<String>() {
			@Override
			public void onFinish(String data, Object context) {
				setPassword(data);
				callback.onFinish(data, null);
			}
		});
	}

	public String getLockPwdOverCatch() {
		return password;
	}

	public void writeLockPwdToFile(String pwd) {
		setPassword(pwd);
	}
}
