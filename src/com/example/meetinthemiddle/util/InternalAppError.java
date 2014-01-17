package com.example.meetinthemiddle.util;

import java.util.concurrent.TimeoutException;

import android.R;
import android.content.Context;

public class InternalAppError extends RuntimeException{
private static final long serialVersionUID = 8756361101145241386L;
	
	private Context ctx;

	public InternalAppError(String msg, Context ctx) {
		super(msg);
		this.ctx = ctx;
	}
	
	public InternalAppError(String msg, Throwable t, Context ctx) {
		super(msg, t);
		this.ctx = ctx;
	}
	
	public String gibUrsache() {
		
		if (this.getCause() == null) {
			return super.toString();
		}
		else {
			String str = "";
			
			Throwable rootCause = this.getCause();
			
			while (rootCause.getCause() != null) {
				rootCause = rootCause.getCause();
			}
			
			if (rootCause instanceof TimeoutException) {
//				str = ctx.getString(R.string.err_verbindung_fehlgeschlagen);
				str = "Verbindung fehlgeschlagen";
			}
			else {
				str += rootCause.toString();
			}
			return str;
		}
	}
}
