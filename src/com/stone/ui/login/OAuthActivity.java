package com.stone.ui.login;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.stone.black.R;
import com.stone.dao.URLHelper;
import com.stone.support.debug.AppLogger;
import com.stone.support.utils.Utility;
import com.stone.ui.interfaces.AbstractAppActivity;

public class OAuthActivity extends AbstractAppActivity {
	private WebView webView;
	private MenuItem refreshItem;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.oauthactivity_layout);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		actionBar.setTitle(getString(R.string.login));
		webView = (WebView) findViewById(R.id.webView);
		webView.setWebViewClient(new WeiboWebViewClient());

		WebSettings settings = webView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setSaveFormData(false);
		settings.setSavePassword(false);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		settings.setRenderPriority(WebSettings.RenderPriority.HIGH);

		CookieSyncManager.createInstance(this);
		CookieManager cookieManager = CookieManager.getInstance();
		cookieManager.removeAllCookie();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TBD
		refresh();
		return true;
	}

	private class WeiboWebViewClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			if (url.startsWith(URLHelper.DIRECT_URL)) {
				handleRedirectUrl(view, url);
				view.stopLoading();
				return;
			}

			super.onPageStarted(view, url, favicon);
		}
	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values = Utility.parseUrl(url);
		String error = values.getString("error");
		String error_code = values.getString("error_code");

		Intent intent = new Intent();
		intent.putExtras(values);

		if (error == null && error_code == null) {
			String access_token = values.getString("access_token");
			String expires_time = values.getString("expires_in");
			setResult(RESULT_OK, intent);
			new OAuthTask(this).execute(access_token, expires_time);
		} else {
			Toast.makeText(OAuthActivity.this,
					getString(R.string.you_cancel_login), Toast.LENGTH_LONG)
					.show();
			finish();
		}
	}

	public void refresh() {
		webView.clearView();
		webView.loadUrl("abount:blank");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// TBD
		webView.loadUrl(getWeiboAuthUrl());
	}

	private String getWeiboAuthUrl() {
		Map<String, String> parameters = new HashMap<String, String>();
		parameters.put("client_id", URLHelper.APP_KEY);
		parameters.put("response_type", "token");
		parameters.put("redirect_uri", URLHelper.DIRECT_URL);
		parameters.put("display", "mobile");
		String url=URLHelper.URL_OAUTH2_ACCESS_AUTHORIZE + "?"
				+ Utility.encodeUrl(parameters)
				+ "&scope=friendships_groups_read,friendships_groups_write";
		AppLogger.d("request url="+url);
		return url;
	}
}
