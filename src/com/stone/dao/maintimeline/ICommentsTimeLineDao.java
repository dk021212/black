package com.stone.dao.maintimeline;

import com.stone.bean.CommentListBean;
import com.stone.support.error.WeiboException;

public interface ICommentsTimeLineDao {
    public CommentListBean getGSONMsgList() throws WeiboException;

    public void setSince_id(String since_id);

    public void setMax_id(String max_id);
}
