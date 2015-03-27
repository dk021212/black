package com.stone.support.utils;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.security.MessageDigest;

import com.stone.support.debug.AppLogger;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;

public class Utility {

	private Utility() {
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
}
