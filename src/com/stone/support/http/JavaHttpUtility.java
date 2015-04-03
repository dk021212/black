package com.stone.support.http;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.crashlytics.android.answers.BuildConfig;
import com.stone.black.R;
import com.stone.support.debug.AppLogger;
import com.stone.support.error.ErrorCode;
import com.stone.support.error.WeiboException;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;

public class JavaHttpUtility {

	private static final int CONNECT_TIMEOUT = 10 * 1000;
	private static final int READ_TIMEOUT = 10 * 1000;

	public class NullHostNameVerifier implements HostnameVerifier {
		public boolean verify(String hostname, SSLSession seesion) {
			return true;
		}
	}

	private TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return null;
		}

		public void checkClientTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}

		public void checkServerTrusted(
				java.security.cert.X509Certificate[] certs, String authType) {
		}
	} };

	public JavaHttpUtility() {
		// allow Android to use an untrusted certificate for SSL/HTTPS
		// connection
		// so that when you debug app, you can use Fiddler http://fidder2.com to
		// logs all HTTPS traffic
		try {
			if (BuildConfig.DEBUG) {
				HttpsURLConnection
						.setDefaultHostnameVerifier(new NullHostNameVerifier());
				SSLContext sc = SSLContext.getInstance("SSL");
				sc.init(null, trustAllCerts, new java.security.SecureRandom());
				HttpsURLConnection.setDefaultSSLSocketFactory(sc
						.getSocketFactory());
			}
		} catch (Exception e) {

		}
	}

	public String executeNormalTask(HttpMethod httpMethod, String url,
			Map<String, String> param) throws WeiboException {

		switch (httpMethod) {
		case Post:
			return doPost(url, param);
		case Get:
			return doGet(url, param);
		}

		return "";

	}

	private static Proxy getProxy() {
		String proxyHost = System.getProperty("http.proxyHost");
		String proxyPort = System.getProperty("http.proxyPort");
		if (!TextUtils.isEmpty(proxyHost) && !TextUtils.isEmpty(proxyPort)) {
			return new Proxy(java.net.Proxy.Type.HTTP, new InetSocketAddress(
					proxyHost, Integer.valueOf(proxyPort)));
		} else {
			return null;
		}
	}

	private String handleResponse(HttpURLConnection httpURLConnection)
			throws WeiboException {
		GlobalContext globalContext = GlobalContext.getInstance();
		String errorStr = globalContext.getString(R.string.timeout);
		globalContext = null;
		int status = 0;

		try {
			status = httpURLConnection.getResponseCode();
		} catch (IOException e) {
			e.printStackTrace();
			httpURLConnection.disconnect();
			throw new WeiboException(errorStr, e);
		}

		if (status != HttpURLConnection.HTTP_OK) {
			return handleError(httpURLConnection);
		}

		return readResult(httpURLConnection);
	}

	private String handleError(HttpURLConnection urlConnection)
			throws WeiboException {
		String result = readError(urlConnection);
		String err = null;
		int errCode = 0;

		try {
			AppLogger.e("error=" + result);
			JSONObject json = new JSONObject(result);
			err = json.optString("error_description", "");
			if (TextUtils.isEmpty(err)) {
				err = json.getString("error");
			}
			errCode = json.getInt("error_code");
			WeiboException exception = new WeiboException();
			exception.setError_code(errCode);
			exception.setOriError(err);

			if (errCode == ErrorCode.EXPIRED_TOKEN) {
				Utility.showExpiredTokenDialogOrNotification();
			}

			throw exception;
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return result;
	}

	private String readResult(HttpURLConnection urlConnection)
			throws WeiboException {
		InputStream is = null;
		BufferedReader buffer = null;
		GlobalContext globalContext = GlobalContext.getInstance();
		String errorStr = globalContext.getString(R.string.timeout);
		globalContext = null;

		try {
			is = urlConnection.getInputStream();

			String content_encode = urlConnection.getContentEncoding();

			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}

			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;

			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}

			AppLogger.d("result=" + strBuilder.toString());
			return strBuilder.toString();
		} catch (Exception e) {
			e.printStackTrace();
			throw new WeiboException(errorStr, e);
		} finally {
			Utility.closeSilently(is);
			Utility.closeSilently(buffer);
			urlConnection.disconnect();
		}
	}

	private String readError(HttpURLConnection urlConnection)
			throws WeiboException {
		InputStream is = null;
		BufferedReader buffer = null;
		GlobalContext globalContext = GlobalContext.getInstance();
		String errorStr = globalContext.getString(R.string.timeout);

		try {
			is = urlConnection.getErrorStream();

			if (is == null) {
				errorStr = globalContext
						.getString(R.string.unknown_sina_network_error);
				throw new WeiboException(errorStr);
			}

			String content_encode = urlConnection.getContentEncoding();

			if (null != content_encode && !"".equals(content_encode)
					&& content_encode.equals("gzip")) {
				is = new GZIPInputStream(is);
			}

			buffer = new BufferedReader(new InputStreamReader(is));
			StringBuilder strBuilder = new StringBuilder();
			String line;

			while ((line = buffer.readLine()) != null) {
				strBuilder.append(line);
			}

			AppLogger.d("error result=" + strBuilder.toString());
			return strBuilder.toString();

		} catch (IOException e) {
			e.printStackTrace();
			throw new WeiboException(errorStr, e);
		} finally {
			Utility.closeSilently(is);
			Utility.closeSilently(buffer);
			urlConnection.disconnect();
			globalContext = null;
		}
	}

	private String doPost(String urlAddress, Map<String, String> param)
			throws WeiboException {
		GlobalContext globalContext = GlobalContext.getInstance();
		String errorStr = globalContext.getString(R.string.timeout);
		globalContext = null;

		try {
			URL url = new URL(urlAddress);
			Proxy proxy = getProxy();
			HttpURLConnection uRLConnection;
			if (proxy != null) {
				uRLConnection = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				uRLConnection = (HttpsURLConnection) url.openConnection();
			}

			uRLConnection.setDoInput(true);
			uRLConnection.setDoOutput(true);
			uRLConnection.setRequestMethod("POST");
			uRLConnection.setUseCaches(false);
			uRLConnection.setConnectTimeout(CONNECT_TIMEOUT);
			uRLConnection.setReadTimeout(READ_TIMEOUT);
			uRLConnection.setInstanceFollowRedirects(false);
			uRLConnection.setRequestProperty("Connection", "Keep-Alive");
			uRLConnection.setRequestProperty("Charset", "UTF-8");
			uRLConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");
			uRLConnection.connect();

			DataOutputStream out = new DataOutputStream(
					uRLConnection.getOutputStream());
			out.write(Utility.encodeUrl(param).getBytes());
			out.flush();
			out.close();
			return handleResponse(uRLConnection);

		} catch (IOException e) {
			e.printStackTrace();
			throw new WeiboException(errorStr, e);
		}
	}

	private String doGet(String urlStr, Map<String, String> param)
			throws WeiboException {
		GlobalContext globalContext = GlobalContext.getInstance();
		String errorStr = globalContext.getString(R.string.timeout);
		globalContext = null;
		InputStream is = null;

		try {
			StringBuilder urlBuilder = new StringBuilder(urlStr);
			urlBuilder.append("?").append(Utility.encodeUrl(param));
			URL url = new URL(urlBuilder.toString());
			AppLogger.d("get request" + url);
			Proxy proxy = getProxy();
			HttpURLConnection urlConnection;
			if (proxy != null) {
				urlConnection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				urlConnection = (HttpURLConnection) url.openConnection();
			}

			urlConnection.setRequestMethod("GET");
			urlConnection.setDoOutput(false);
			urlConnection.setConnectTimeout(CONNECT_TIMEOUT);
			urlConnection.setReadTimeout(READ_TIMEOUT);
			urlConnection.setRequestProperty("Connection", "Keep-Alive");
			urlConnection.setRequestProperty("Charset", "UTF-8");
			urlConnection
					.setRequestProperty("Accept-Encoding", "gzip, deflate");

			urlConnection.connect();

			return handleResponse(urlConnection);
		} catch (IOException e) {
			e.printStackTrace();
			throw new WeiboException(errorStr, e);
		}
	}
}
