package com.stone.support.asyncdrawable;

import com.stone.support.utils.ThemeUtility;

import android.os.Handler;

public class TimeLineBitmapDownloader {
	
	private Handler handler;
	private int defaultPictureResId;
	
	private static TimeLineBitmapDownloader instance;
	
	private TimeLineBitmapDownloader(Handler handler){
		this.handler=handler;
	}

}
