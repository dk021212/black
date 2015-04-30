package com.stone.support.utils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.crashlytics.android.answers.BuildConfig;
import com.stone.bean.AccountBean;
import com.stone.bean.MessageBean;
import com.stone.black.R;
import com.stone.othercomponent.unreadnotification.NotificationServiceHelper;
import com.stone.support.debug.AppLogger;
import com.stone.support.error.WeiboException;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.file.FileManager;
import com.stone.support.lib.AutoScrollListView;
import com.stone.support.lib.MyAsyncTask;
import com.stone.ui.blackmagic.BlackMagicActivity;
import com.stone.ui.login.AccountActivity;
import com.stone.ui.login.OAuthActivity;
import com.stone.ui.login.SSOActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.HapticFeedbackConstants;
import android.view.View;
import android.widget.ListView;
import android.widget.ShareActionProvider;

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

	public static void printStackTrace(Exception e) {
		if (BuildConfig.DEBUG) {
			e.printStackTrace();
		}
	}

	public static int dip2px(int dipValue) {
		float reSize = GlobalContext.getInstance().getResources()
				.getDisplayMetrics().density;
		return (int) ((dipValue * reSize) + 0.5);
	}

	public static int px2dip(int pxValue) {
		float reSize = GlobalContext.getInstance().getResources()
				.getDisplayMetrics().density;
		return (int) ((pxValue / reSize) + 0.5);
	}

	public static float sp2px(int spValue) {
		return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue,
				GlobalContext.getInstance().getResources().getDisplayMetrics());
	}

	public static boolean isWeiboAccountIdLink(String url) {
		url = convertWeiboCnToWeiboCom(url);
		return !TextUtils.isEmpty(url) && url.startsWith("http://weibo.com/u/");
	}

	public static String getIdFromWeiboAccountLink(String url) {

		url = convertWeiboCnToWeiboCom(url);

		String id = url.substring("http://weibo.com/u/".length());
		id = id.replace("/", "");
		return id;
	}

	// todo need refactor...
	public static boolean isWeiboAccountDomainLink(String url) {
		if (TextUtils.isEmpty(url)) {
			return false;
		} else {
			url = convertWeiboCnToWeiboCom(url);
			boolean a = url.startsWith("http://weibo.com/")
					|| url.startsWith("http://e.weibo.com/");
			boolean b = !url.contains("?");

			String tmp = url;
			if (tmp.endsWith("/")) {
				tmp = tmp.substring(0, tmp.lastIndexOf("/"));
			}

			int count = 0;
			char[] value = tmp.toCharArray();
			for (char c : value) {
				if ("/".equalsIgnoreCase(String.valueOf(c))) {
					count++;
				}
			}
			return a && b && count == 3;
		}
	}

	public static String getDomainFromWeiboAccountLink(String url) {
		url = convertWeiboCnToWeiboCom(url);

		final String NORMAL_DOMAIN_PREFIX = "http://weibo.com/";
		final String ENTERPRISE_DOMAIN_PREFIX = "http://e.weibo.com/";

		if (TextUtils.isEmpty(url)) {
			throw new IllegalArgumentException("Url can't be empty");
		}

		if (!url.startsWith(NORMAL_DOMAIN_PREFIX)
				&& !url.startsWith(ENTERPRISE_DOMAIN_PREFIX)) {
			throw new IllegalArgumentException("Url must start with "
					+ NORMAL_DOMAIN_PREFIX + " or " + ENTERPRISE_DOMAIN_PREFIX);
		}

		String domain = null;
		if (url.startsWith(ENTERPRISE_DOMAIN_PREFIX)) {
			domain = url.substring(ENTERPRISE_DOMAIN_PREFIX.length());

		} else if (url.startsWith(NORMAL_DOMAIN_PREFIX)) {
			domain = url.substring(NORMAL_DOMAIN_PREFIX.length());
		}
		domain = domain.replace("/", "");
		return domain;
	}

	private static String convertWeiboCnToWeiboCom(String url) {
		if (!TextUtils.isEmpty(url)) {
			if (url.startsWith("http://weibo.cn")) {
				url = url.replace("http://weibo.cn", "http://weibo.com");
			} else if (url.startsWith("http://www.weibo.com")) {
				url = url.replace("http://www.weibo.com", "http://weibo.com");
			} else if (url.startsWith("http://www.weibo.cn")) {
				url = url.replace("http://www.weibo.cn", "http://weibo.com");
			}
		}
		return url;
	}

	public static void vibrate(Context context, View view) {
		view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

	}

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		return networkInfo != null && networkInfo.isConnected();
	}

	public static boolean isWifi(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
				return true;
			}
		}

		return false;
	}

	public static int getScreenWidth() {
		Activity activity = GlobalContext.getInstance().getActivity();
		if (activity != null) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			return metrics.widthPixels;
		}

		return 480;
	}

	public static int getScreenHeight() {
		Activity activity = GlobalContext.getInstance().getActivity();
		if (activity != null) {
			Display display = activity.getWindowManager().getDefaultDisplay();
			DisplayMetrics metrics = new DisplayMetrics();
			display.getMetrics(metrics);
			return metrics.heightPixels;
		}
		return 800;
	}

	public static void unregisterReceiverIgnoredReceiverNotRegisteredException(
			Context context, BroadcastReceiver broadcastReceiver) {
		try {
			context.getApplicationContext().unregisterReceiver(
					broadcastReceiver);
		} catch (IllegalArgumentException receiverNotRegisteredException) {
			receiverNotRegisteredException.printStackTrace();
		}
	}

	public static boolean isSystemRinger(Context context) {
		AudioManager manager = (AudioManager) context
				.getSystemService(Context.AUDIO_SERVICE);
		return manager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
	}

	public static boolean isDevicePort() {
		return GlobalContext.getInstance().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
	}

	public static void stopListViewScrollingAndScrollTop(ListView listView) {
		if (listView instanceof AutoScrollListView) {
			((AutoScrollListView) listView).requestPositionToScreen(0, true);
		} else {
			listView.smoothScrollToPosition(0, 0);
		}
	}

	public static boolean isAllNotNull(Object... obs) {
		for (int i = 0; i < obs.length; i++) {
			if (obs[i] == null) {
				return false;
			}
		}

		return true;
	}

	public static View getListViewItemViewFromPosition(ListView listView,
			int position) {
		return listView.getChildAt(position
				- listView.getFirstVisiblePosition());
	}

	public static boolean isIntentSafe(Activity activity, Uri uri) {
		Intent mapCall = new Intent(Intent.ACTION_VIEW, uri);
		PackageManager packageManager = activity.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(
				mapCall, 0);
		return activities.size() > 0;
	}

	public static boolean isIntentSafe(Activity activity, Intent intent) {
		PackageManager packageManager = activity.getPackageManager();
		List<ResolveInfo> activities = packageManager.queryIntentActivities(
				intent, 0);
		return activities.size() > 0;
	}

	public static void setShareIntent(Activity activity,
			ShareActionProvider mShareActionProvider, MessageBean msg) {
		Intent shareIntent = new Intent(Intent.ACTION_SEND);
		if (msg != null && msg.getUser() != null) {
			shareIntent.setType("text/plain");
			shareIntent.putExtra(Intent.EXTRA_TEXT, "@"
					+ msg.getUser().getScreen_name() + ":" + msg.getText());
			if (!TextUtils.isEmpty(msg.getThumbnail_pic())) {
				Uri picUrl = null;
				String smallPath = FileManager.getFilePathFromUrl(
						msg.getThumbnail_pic(),
						FileLocationMethod.picture_thumbnail);
				String middlePath = FileManager.getFilePathFromUrl(
						msg.getBmiddle_pic(),
						FileLocationMethod.picture_bmiddle);
				String largePath = FileManager
						.getFilePathFromUrl(msg.getOriginal_pic(),
								FileLocationMethod.picture_large);
				if (new File(largePath).exists()) {
					picUrl = Uri.fromFile(new File(largePath));
				} else if (new File(middlePath).exists()) {
					picUrl = Uri.fromFile(new File(middlePath));
				} else if (new File(smallPath).exists()) {
					picUrl = Uri.fromFile(new File(smallPath));
				}

				if (picUrl != null) {
					shareIntent.putExtra(Intent.EXTRA_STREAM, picUrl);
					shareIntent.setType("image/*");
				}
			}
			if (Utility.isIntentSafe(activity, shareIntent)
					&& mShareActionProvider != null) {
				mShareActionProvider.setShareIntent(shareIntent);
			}
		}
	}
	
	public static boolean isTaskStopped(MyAsyncTask task) {
        return task == null || task.getStatus() == MyAsyncTask.Status.FINISHED;
    }
}
