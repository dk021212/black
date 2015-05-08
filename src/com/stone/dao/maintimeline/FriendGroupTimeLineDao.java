package com.stone.dao.maintimeline;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stone.bean.MessageListBean;
import com.stone.dao.URLHelper;
import com.stone.support.debug.AppLogger;
import com.stone.support.error.WeiboException;
import com.stone.support.http.HttpMethod;
import com.stone.support.http.HttpUtility;
import com.stone.support.utils.TimeLineUtility;

public class FriendGroupTimeLineDao extends MainFriendsTimeLineDao {

    protected String getUrl() {
        return URLHelper.FRIENDSGROUP_TIMELINE;
    }

    private String getMsgListJson() throws WeiboException {
        String url = getUrl();

        Map<String, String> map = new HashMap<String, String>();
        map.put("access_token", access_token);
        map.put("since_id", since_id);
        map.put("max_id", max_id);
        map.put("count", count);
        map.put("page", page);
        map.put("base_app", base_app);
        map.put("feature", feature);
        map.put("trim_user", trim_user);
        map.put("list_id", list_id);

        String jsonData = HttpUtility.getInstance().executeNormalTask(HttpMethod.Get, url, map);

        return jsonData;
    }

    public MessageListBean getGSONMsgList() throws WeiboException {

        String json = getMsgListJson();
        Gson gson = new Gson();

        MessageListBean value = null;
        try {
            value = gson.fromJson(json, MessageListBean.class);
        } catch (JsonSyntaxException e) {

            AppLogger.e(e.getMessage());
            return null;
        }
        if (value != null && value.getItemList().size() > 0) {
            TimeLineUtility.filterMessage(value);

        }


        return value;
    }


    public FriendGroupTimeLineDao(String access_token, String list_id) {

        super(access_token);
        this.list_id = list_id;
    }

    private String list_id;
}

