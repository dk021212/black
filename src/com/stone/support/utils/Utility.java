package com.stone.support.utils;

import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.stone.bean.AccountBean;
import com.stone.black.R;
import com.stone.othercomponent.unreadnotification.NotificationServiceHelper;
import com.stone.support.debug.AppLogger;
import com.stone.ui.blackmagic.BlackMagicActivity;
import com.stone.ui.login.AccountActivity;
import com.stone.ui.login.OAuthActivity;
import com.stone.ui.login.SSOActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;

public class Utility {

	private Utility() {
	}

	public static String encodeUrl(Map<String, String> param) {
		if (param == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder();
		Set<String> keys = param.keySet();
		boolean first = true;

		for (String key : keys) {
			String value = param.get(key);
			// pain...EditMyProfileDao params' values can be empty
			if (!TextUtils.isEmpty(value) || key.equals("description")
					|| key.equals("url")) {
				if (first) {
					first = false;
				} else {
					sb.append("&");
				}
				try {
					sb.append(URLEncoder.encode(key, "UTF-8")).append("=")
							.append(URLEncoder.encode(param.get(key), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		return sb.toString();
	}

	// if app's certificate md5 is correct
	public static boolean isCertificateFingerprintCorrect(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			String packageName = context.getPackageName();
			int flags = PackageManager.GET_SIGNATURES;

			PackageInfo packageInfo = pm.getPackageInfo(packageName, flags);
			Signature[] signatures = packageInfo.signatures;
			byte[] cert = signatures[0].toByteArray();
			String strResult = "";

			MessageDigest md;
			md = MessageDigest.getInstance("MD5");
			md.update(cert);
			for (byte b : md.digest()) {
				strResult += Integer.toString(b & 0xff, 16);
			}

			strResult = strResult.toUpperCase();

			// debug
			if ("4F1442D83DEAE5A49CD19D9E0F52C5D".toUpperCase().equals(
					strResult)) {
				return true;
			}
			// relaease
			// if
			// ("C96155C3DAD4CA1069808F0BAC813A69".toUpperCase().equals(strResult))
			// {
			// return true;
			// }
			AppLogger.e(strResult);
		} catch (Exception ex) {

		}
		return false;
	}

	/**
	 * Parse a URL query and fragment parameters into a key-value bundle.
	 */
	public static Bundle parseUrl(String url) {
		// hack to pervent MalformedURLException
		url = url.replace("weiboconnect", "http");
		try {
			URL u = new URL(url);
			Bundle b = decodeUrl(u.getQuery());
			b.putAll(decodeUrl(u.getRef()));
			return b;
		} catch (MalformedURLException e) {
			return new Bundle();
		}
	}

	private static Bundle decodeUrl(String s) {
		Bundle params = new Bundle();

		if (s != null) {
			String array[] = s.split("&");
			for (String param : array) {
				String v[] = param.split("=");
				try {
					params.putString(URLDecoder.decode(v[0], "UTF-8"),
							URLDecoder.decode(v[1], "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}
		return params;
	}

	public static boolean isTokenValid(AccountBean account) {
		return !TextUtils.isEmpty(account.getAccess_token())
				&& (account.getExpires_time() == 0 || (System
						.currentTimeMillis()) < account.getExpires_time());
	}

	public static void showExpiredTokenDialogOrNotification() {
		final Activity activity = GlobalContext.getInstance()
				.getCurrentRunningActivity();
		boolean currentAccountTokenIsExpired = true;
		AccountBean currentAccount = GlobalContext.getInstance()
				.getAccountBean();
		if (currentAccount != null) {
			currentAccountTokenIsExpired = !Utility
					.isTokenValid(currentAccount);
		}

		if (currentAccountTokenIsExpired && activity != null
				&& !GlobalContext.getInstance().tokenExpiredDialogIsShowing) {
			if (activity.getClass() == AccountActivity.class) {
				return;
			}
			if (activity.getClass() == OAuthActivity.class) {
				return;
			}
			if (activity.getClass() == BlackMagicActivity.class) {
				return;
			}
			if (activity.getClass() == SSOActivity.class) {
				return;
			}

			activity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new AlertDialog.Builder(activity)
							.setTitle(R.string.dialog_title_error)
							.setMessage(R.string.your_token_is_expired)
							.setPositiveButton(R.string.logout_to_login_again,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											Intent intent = new Intent(
													activity,
													AccountActivity.class);
											intent.putExtra("launcher", false);
											activity.startActivity(intent);
											activity.finish();
											GlobalContext.getInstance().tokenExpiredDialogIsShowing = false;

										}
									})
							.setOnCancelListener(
									new DialogInterface.OnCancelListener() {

										@Override
										public void onCancel(
												DialogInterface dialog) {
											// do nothing;

										}
									}).show();
					GlobalContext.getInstance().tokenExpiredDialogIsShowing = true;
				}
			});
		} else if (!currentAccountTokenIsExpired || activity == null) {
			Intent i = AccountActivity.newItent();
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
					| Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent pendingIntent = PendingIntent.getActivity(
					GlobalContext.getInstance(), 0, i,
					PendingIntent.FLAG_UPDATE_CURRENT);

			Notification.Builder builder = new Notification.Builder(
					GlobalContext.getInstance())
					.setContentTitle(
							GlobalContext.getInstance().getString(
									R.string.login_again))
					.setContentText(
							GlobalContext
									.getInstance()
									.getString(
											R.string.have_account_whose_token_is_expired))
					.setSmallIcon(R.drawable.ic_notification)
					.setAutoCancel(true).setContentIntent(pendingIntent)
					.setOnlyAlertOnce(true);
			NotificationManager notificationManager = (NotificationManager) GlobalContext
					.getInstance().getSystemService(
							Context.NOTIFICATION_SERVICE);
			notificationManager.notify(
					NotificationServiceHelper.getTokenExpiredNotificationId(),
					builder.build());
		} else if (GlobalContext.getInstance().tokenExpiredDialogIsShowing) {
			NotificationManager notificationManager = (NotificationManager) GlobalContext
					.getInstance().getSystemService(
							Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NotificationServiceHelper
					.getTokenExpiredNotificationId());
		}
	}

	public static void closeSilently(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				AppLogger.e("close error: " + e.getMessage());
			}
		}
	}

	public static long calcTokenExpiresInDays(AccountBean account) {
		long days = TimeUnit.MILLISECONDS.toDays(account.getExpires_time()
				- System.currentTimeMillis());
		return days;
	}
}
