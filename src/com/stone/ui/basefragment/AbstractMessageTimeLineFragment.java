package com.stone.ui.basefragment;

import com.stone.bean.ListBean;
import com.stone.bean.MessageBean;

public abstract class AbstractMessageTimeLineFragment<T extends ListBean<MessageBean, ?>>
		extends AbstractTimeLineFragment<T> {
	
	public void clearActionMode() {
		
	}

}
