package com.stone.support.http;

import java.util.Map;

import com.stone.support.error.WeiboException;

public class HttpUtility {

	private static HttpUtility httpUtility = new HttpUtility();

	private HttpUtility() {

	}

	public static HttpUtility getInstance() {
		return httpUtility;
	}

	public String executeNormalTask(HttpMethod httpMethod, String url,
			Map<String, String> param) throws WeiboException {
		return new JavaHttpUtility().executeNormalTask(httpMethod, url, param);
	}

}
