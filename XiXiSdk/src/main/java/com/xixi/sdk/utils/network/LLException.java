package com.xixi.sdk.utils.network;

public class LLException {

	public static class LLMalFormatOfJsonException extends Exception{

		public LLMalFormatOfJsonException(String json) { 
			super("incorrect json " + json ) ; 
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = -8909198701378991082L; 
		
	}
	
	public static class LLIncorrectHttpCodeException extends Exception {
 
		public LLIncorrectHttpCodeException(int errorCode) { 
			super("incorrect code " + errorCode ) ; 
		}
		
		private static final long serialVersionUID = -8004625233027777367L; 
		
	}
	
	public static class LLCmdErrorException extends Exception {
		public LLCmdErrorException(String strErrorMsg ) { 
			super("error msg " + strErrorMsg ) ; 
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 5368454650077761591L; 
		
	}
	public static class LLNetworkException extends Exception{
		public LLNetworkException(String netWorkerror ) { 
			super("error msg " + netWorkerror ) ; 
		}
		/**
		 * 
		 */
		private static final long serialVersionUID = 1559758532330791584L;
		
	}
}
