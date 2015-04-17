package com.stone.ui.main;

import android.os.Bundle;

import com.stone.ui.interfaces.AbstractAppFragment;

public class CommentsTimeLine extends AbstractAppFragment {

	public static CommentsTimeLine newInstance() {
        CommentsTimeLine fragment = new CommentsTimeLine();
        fragment.setArguments(new Bundle());
        return fragment;
    }
	
	public void clearActionMode() {
		
	}

}
