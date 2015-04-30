package com.stone.ui.userinfo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.stone.bean.ListBean;
import com.stone.ui.basefragment.AbstractMessageTimeLineFragment;

public class MyFavListFragment extends AbstractMessageTimeLineFragment {

	public static MyFavListFragment newInstance() {
        MyFavListFragment fragment = new MyFavListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

	@Override
	public ListBean getList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void newMsgOnPostExecute(ListBean newValue, Bundle loaderArgs) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void oldMsgOnPostExecute(ListBean newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void buildListAdapter() {
		// TODO Auto-generated method stub
		
	}

}
