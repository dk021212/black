package com.stone.ui.userinfo;

import android.os.Bundle;

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

}
