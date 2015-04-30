package com.stone.ui.userinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.stone.bean.MessageListBean;
import com.stone.bean.UserBean;
import com.stone.ui.basefragment.AbstractMessageTimeLineFragment;

public class NewUserInfoFragment extends AbstractMessageTimeLineFragment<MessageListBean> {
	
	protected UserBean userBean;

    protected String token;
	
	public static NewUserInfoFragment newInstance(UserBean userBean, String token) {
        NewUserInfoFragment fragment = new NewUserInfoFragment(userBean, token);
        fragment.setArguments(new Bundle());
        return fragment;
    }


    public NewUserInfoFragment() {

    }
    
    public NewUserInfoFragment(UserBean userBean, String token) {
        this.userBean = userBean;
        this.token = token;
    }


	@Override
	public MessageListBean getList() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void newMsgOnPostExecute(MessageListBean newValue,
			Bundle loaderArgs) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void oldMsgOnPostExecute(MessageListBean newValue) {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void buildListAdapter() {
		// TODO Auto-generated method stub
		
	}

}
