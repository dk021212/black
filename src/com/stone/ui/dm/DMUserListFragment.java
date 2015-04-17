package com.stone.ui.dm;

import android.os.Bundle;

import com.stone.ui.basefragment.AbstractTimeLineFragment;

public class DMUserListFragment extends AbstractTimeLineFragment {

	public static DMUserListFragment newInstance() {
        DMUserListFragment fragment = new DMUserListFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

}
