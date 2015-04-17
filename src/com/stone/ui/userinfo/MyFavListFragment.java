package com.stone.ui.userinfo;

import android.os.Bundle;

import com.stone.ui.basefragment.AbstractMessageTimeLineFragment;

public class MyFavListFragment extends AbstractMessageTimeLineFragment {

	public static MyFavListFragment newInstance() {
        MyFavListFragment fragment = new MyFavListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

}
