package com.stone.support.utils;

import com.stone.bean.AccountBean;
import com.stone.bean.UserBean;

public class ObjectToStringUtility {

	public static String toString(UserBean bean) {
		return "user id=" + bean.getId() + "," + "name="
				+ bean.getScreen_name();
	}

	public static String toString(AccountBean account) {
        return account.getUsernick();
    }

}
