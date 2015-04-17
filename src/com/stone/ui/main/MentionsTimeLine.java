package com.stone.ui.main;

import android.os.Bundle;

import com.stone.ui.interfaces.AbstractAppFragment;

public class MentionsTimeLine extends AbstractAppFragment {

	public static MentionsTimeLine newInstance() {
        MentionsTimeLine fragment = new MentionsTimeLine();
        fragment.setArguments(new Bundle());
        return fragment;
    }
	
	public void clearActionMode() {
		
	}
}
