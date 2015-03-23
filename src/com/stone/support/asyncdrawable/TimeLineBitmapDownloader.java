package com.stone.support.asyncdrawable;

import com.stone.black.R;
import com.stone.support.utils.ThemeUtility;

import android.os.Handler;
import android.os.Looper;

public class TimeLineBitmapDownloader {

	private Handler handler;
	private int defaultPictureResId;

	private static TimeLineBitmapDownloader instance;
	private static final Object lock = new Object();

	private TimeLineBitmapDownloader(Handler handler) {
		this.handler = handler;
		this.defaultPictureResId = ThemeUtility
				.getResourceId(R.attr.listview_pic_bg);
	}

	public static TimeLineBitmapDownloader getInstance() {
		synchronized (lock) {
			if (instance == null) {
				instance = new TimeLineBitmapDownloader(new Handler(
						Looper.getMainLooper()));
			}
		}

		return instance;
	}

	public static void refreshThemePictureBackground() {
		synchronized (lock) {
			instance = new TimeLineBitmapDownloader(new Handler(
					Looper.getMainLooper()));
		}
	}

}
