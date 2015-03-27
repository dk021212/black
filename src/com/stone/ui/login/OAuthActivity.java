package com.stone.ui.login;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.stone.dao.URLHelper;
import com.stone.support.utils.Utility;
import com.stone.ui.interfaces.AbstractAppActivity;

public class OAuthActivity extends AbstractAppActivity {
	private WebView webView;
	private MenuItem refreshItem;
	
	private class WeiboWebViewClient extends WebViewClient{
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view,String url){
			view.loadUrl(url);
			return true;
		}
		
		@Override
		public void onPageStarted(WebView view,String url,Bitmap favicon){
			if(url.startsWith(URLHelper.DIRECT_URL)){
				handleRedirectUrl(view,url);
			}
		}
	}

	private void handleRedirectUrl(WebView view, String url) {
		Bundle values=Utility.parseUrl(url);
		String error=values.getString("error");
		String error_code=values.getString("error_code");
		
		Intent intent=new Intent();
		intent.putExtras(values);
		
		if(error==null && error_code==null){
			String access_token=values.getString("access_token");
			String expires_time=values.getString("expires_in");
		}
	}
}
