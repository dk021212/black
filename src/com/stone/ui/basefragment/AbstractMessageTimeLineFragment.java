package com.stone.ui.basefragment;

import android.widget.ProgressBar;
import android.widget.TextView;

import com.stone.bean.ListBean;
import com.stone.bean.MessageBean;
import com.stone.support.lib.pulltorefresh.PullToRefreshListView;

/**
 * User: Stone
 * Date: 15-04-17
 * balck has two kinds of methods to send/receive network request/response asynchronously,
 * one is setRetainInstance(true) + AsyncTask, the other is AsyncTaskLoader
 * Because nested fragment(parent fragment has a viewpager, viewpager has many children fragments,
 * these children fragments are called nested fragment) can't use setRetainInstance(true), at this
 * moment
 * you have to use AsyncTaskLoader to solve Android configuration change(for example: change screen
 * orientation,
 * change system language)
 */
public abstract class AbstractMessageTimeLineFragment<T extends ListBean<MessageBean, ?>>
		extends AbstractTimeLineFragment<T> {
	
	
	protected PullToRefreshListView pullToRefreshListView;
	
	protected TextView empty;
	
	protected ProgressBar progressBar;
	
	protected TopTipBar newMsgTipBar;
	
	public void clearActionMode() {
		
	}

}
