package com.stone.ui.search;

import android.os.Bundle;

import com.stone.ui.interfaces.AbstractAppFragment;

public class SearchMainParentFragment extends AbstractAppFragment {

	public static SearchMainParentFragment newInstance() {
        SearchMainParentFragment fragment = new SearchMainParentFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }
	
	public void clearActionMode() {
		
	}

}
