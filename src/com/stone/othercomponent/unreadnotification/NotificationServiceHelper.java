package com.stone.othercomponent.unreadnotification;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class NotificationServiceHelper extends Service {

	private static final int TOKEN_EXPIRED_NOTIFICATION_ID = 2013;

	public static int getTokenExpiredNotificationId() {
		return TOKEN_EXPIRED_NOTIFICATION_ID;
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
