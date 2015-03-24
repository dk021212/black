package com.stone.support.crashmanager;

import android.content.Context;

public class CrashManagerConstants {

	static String API_VERSION = null;
	static String APP_PACKAGE = null;
	static String ANDROID_VERSION = null;
	static String PHONE_MODEL = null;
	static String PHONE_MANUFACTURER = null;

	public static void loadFromContext(Context context) {
		CrashManagerConstants.ANDROID_VERSION = android.os.Build.VERSION.RELEASE;
	}
}
