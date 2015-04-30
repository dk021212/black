package com.stone.dao.destroy;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stone.bean.MessageBean;
import com.stone.dao.URLHelper;
import com.stone.support.debug.AppLogger;
import com.stone.support.error.WeiboException;
import com.stone.support.http.HttpMethod;
import com.stone.support.http.HttpUtility;

public class DestroyStatusDao {

	private String access_token;
	private String id;

	public DestroyStatusDao(String access_token, String id) {
		this.access_token = access_token;
		this.id = id;
	}

	public boolean destroy() throws WeiboException {
		String url = URLHelper.STATUSES_DESTROY;
		Map<String, String> map = new HashMap<String, String>();
		map.put("access_token", access_token);
		map.put("id", id);
		
		String jsonData=HttpUtility.getInstance().executeNormalTask(HttpMethod.Post, url, map);
		Gson gson=new Gson();
		
		try{
			MessageBean value=gson.fromJson(jsonData, MessageBean.class);
		}catch(JsonSyntaxException e){
			AppLogger.e(e.getMessage());
			return false;
		}
		
		return true;
	}

}
