package com.stone.support.lib;

import com.stone.black.R;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;
import com.stone.support.utils.WebBrowserSelector;
import com.stone.ui.adapter.LongClickLinkDialog;
import com.stone.ui.userinfo.UserInfoActivity;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Parcel;
import android.provider.Browser;
import android.support.v4.app.FragmentActivity;
import android.text.ParcelableSpan;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.view.View;

public class MyURLSpan extends ClickableSpan implements ParcelableSpan {

	private final String mURL;

	public MyURLSpan(String url) {
		mURL = url;
	}

	public MyURLSpan(Parcel src) {
		mURL = src.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(mURL);

	}

	@Override
	public int getSpanTypeId() {
		return 11;
	}

	public String getURL() {
		return mURL;
	}

	@Override
	public void onClick(View widget) {
		Uri uri = Uri.parse(getURL());
		Context context = widget.getContext();
		if (uri.getScheme().startsWith("http")) {
			String url = uri.toString();
			if (Utility.isWeiboAccountIdLink(url)) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("id", Utility.getIdFromWeiboAccountLink(url));
				context.startActivity(intent);
			} else if (Utility.isWeiboAccountDomainLink(url)) {
				Intent intent = new Intent(context, UserInfoActivity.class);
				intent.putExtra("domain",
						Utility.getDomainFromWeiboAccountLink(url));
				context.startActivity(intent);
			} else {
				// otherwise some urls can be opened, will be redirected to sina
				// error page
				String openUrl = url;
				if (openUrl.endsWith("/")) {
					openUrl = openUrl.substring(0, openUrl.lastIndexOf("/"));
				}
				WebBrowserSelector.openLink(context, Uri.parse(openUrl));
			}
		} else {
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			intent.putExtra(Browser.EXTRA_APPLICATION_ID,
					context.getPackageName());
			context.startActivity(intent);
		}
	}
	
	public void onLongClick(View widget){
		Uri data=Uri.parse(getURL());
		if(data!=null){
			String d=data.toString();
			String newValue="";
			if(d.startsWith("com.stone.black")){
				int index=d.lastIndexOf("/");
				newValue=d.substring(index+1);
			}else if(d.startsWith("http")){
				newValue=d;
			}
			
			if(!TextUtils.isEmpty(newValue)){
				Utility.vibrate(widget.getContext(),widget);
				LongClickLinkDialog dialog=new LongClickLinkDialog(data);
				dialog.show(((FragmentActivity)widget.getContext()).getSupportFragmentManager(),"");
			}
		}
	}
	
	@Override
	public void updateDrawState(TextPaint tp){
		int[] attrs=new int[]{R.attr.link_color};
		TypedArray ta=GlobalContext.getInstance().getActivity().obtainStyledAttributes(attrs);
		int drawableFromTheme=ta.getColor(0, 430);
		tp.setColor(drawableFromTheme);
	}

}
