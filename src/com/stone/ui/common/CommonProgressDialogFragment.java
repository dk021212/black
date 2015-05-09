package com.stone.ui.common;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class CommonProgressDialogFragment extends DialogFragment {
	
	public CommonProgressDialogFragment() {

	}
	
	public static CommonProgressDialogFragment newInstance(String content){
		CommonProgressDialogFragment fragment=new CommonProgressDialogFragment();
		Bundle bundle=new Bundle();
		bundle.putString("Content", content);
		fragment.setArguments(bundle);
		return fragment;
	}
}
