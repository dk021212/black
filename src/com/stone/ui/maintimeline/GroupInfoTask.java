package com.stone.ui.maintimeline;

import com.stone.bean.GroupListBean;
import com.stone.dao.maintimeline.FriendGroupDao;
import com.stone.support.database.GroupDBTask;
import com.stone.support.error.WeiboException;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.utils.GlobalContext;

public class GroupInfoTask extends MyAsyncTask<Void,GroupListBean,GroupListBean> {

	 private WeiboException e;

	    private String token;
	    private String accountId;

	    public GroupInfoTask(String token, String accountId) {
	        this.token = token;
	        this.accountId = accountId;
	    }

	    @Override
	    protected GroupListBean doInBackground(Void... params) {
	        try {
	            return new FriendGroupDao(token).getGroup();
	        } catch (WeiboException e) {
	            this.e = e;
	            cancel(true);
	        }
	        return null;
	    }


	    @Override
	    protected void onPostExecute(GroupListBean groupListBean) {
	        super.onPostExecute(groupListBean);

	        GroupDBTask.update(groupListBean, accountId);
	        if (accountId.equalsIgnoreCase(GlobalContext.getInstance().getCurrentAccountId()))
	            GlobalContext.getInstance().setGroup(groupListBean);

	    }

}
