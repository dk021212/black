package com.stone.ui.maintimeline;

import android.os.Bundle;

import com.stone.bean.AccountBean;
import com.stone.bean.MessageListBean;
import com.stone.bean.UserBean;
import com.stone.ui.basefragment.AbstractMessageTimeLineFragment;

public class FriendsTimeLineFragment extends
		AbstractMessageTimeLineFragment<MessageListBean> {

	private AccountBean accountBean;
	private UserBean userBean;
	private String token;

	public static FriendsTimeLineFragment newInstance(AccountBean accountBean,
			UserBean userBean, String token) {
		FriendsTimeLineFragment fragment = new FriendsTimeLineFragment(
				accountBean, userBean, token);
		fragment.setArguments(new Bundle());
		return fragment;
	}

	public FriendsTimeLineFragment(AccountBean accountBean, UserBean userBean,
			String token) {
		this.accountBean = accountBean;
		this.userBean = userBean;
		this.token = token;
	}

	public void buildActionBarNav() {
		// TODO Auto-generated method stub
		
	}
}
