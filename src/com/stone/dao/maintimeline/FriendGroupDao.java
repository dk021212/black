package com.stone.dao.maintimeline;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stone.bean.GroupListBean;
import com.stone.dao.URLHelper;
import com.stone.support.debug.AppLogger;
import com.stone.support.error.WeiboException;
import com.stone.support.http.HttpMethod;
import com.stone.support.http.HttpUtility;

public class FriendGroupDao {

	public GroupListBean getGroup() throws WeiboException {

		String url = URLHelper.FRIENDSGROUP_INFO;

		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);

		String jsonData = HttpUtility.getInstance().executeNormalTask(
				HttpMethod.Get, url, map);

		Gson gson = new Gson();

		GroupListBean value = null;
		try {
			value = gson.fromJson(jsonData, GroupListBean.class);
		} catch (JsonSyntaxException e) {
			AppLogger.e(e.getMessage());
		}

		return value;
	}

	public FriendGroupDao(String token) {
		this.access_token = token;
	}

	private String access_token;

}
