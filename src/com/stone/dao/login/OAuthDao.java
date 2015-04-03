package com.stone.dao.login;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stone.bean.UserBean;
import com.stone.dao.URLHelper;
import com.stone.support.debug.AppLogger;
import com.stone.support.error.WeiboException;
import com.stone.support.http.HttpMethod;
import com.stone.support.http.HttpUtility;

public class OAuthDao {

	private String access_token;

	public OAuthDao(String access_token) {
		this.access_token = access_token;
	}

	public UserBean getOAuthUserInfo() throws WeiboException {
		String uidJson = getOAuthUserUIDJsonData();
		String uid = "";

		try {
			JSONObject jsonObject = new JSONObject(uidJson);
			uid = jsonObject.optString("uid");
		} catch (JSONException e) {
			AppLogger.e(e.getMessage());
		}

		Map<String, String> map = new HashMap<String, String>();
		map.put("uid", uid);
		map.put("access_token", access_token);

		String url = URLHelper.USER_SHOW;

		String result = HttpUtility.getInstance().executeNormalTask(
				HttpMethod.Get, url, map);

		Gson gson = new Gson();
		UserBean user = new UserBean();

		try {
			user = gson.fromJson(result, UserBean.class);
		} catch (JsonSyntaxException e) {
			AppLogger.e(result);
		}

		return user;
	}

	private String getOAuthUserUIDJsonData() throws WeiboException {
		String url = URLHelper.UID;
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);

		return HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url,
				map);
	}

}
