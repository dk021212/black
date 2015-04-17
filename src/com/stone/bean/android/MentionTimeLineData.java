package com.stone.bean.android;

import com.stone.bean.MessageListBean;

public class MentionTimeLineData {
	 public MessageListBean msgList;
	    public TimeLinePosition position;

	    public MentionTimeLineData(MessageListBean msgList, TimeLinePosition position) {
	        this.msgList = msgList;
	        this.position = position;
	    }
}
