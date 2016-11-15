package com.demo.nfc.utils;

@SuppressWarnings("serial")
public class AppException extends RuntimeException {

	public AppException(){
		
	}
	public AppException(String msg){
		super(msg);
	}
    public AppException(Throwable cause) {
        super(cause);
    }
}
