package com.stone.ui.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState){
		ProgressDialog dialog=new ProgressDialog(getActivity());
		dialog.setMessage(getArguments().getString("Content"));
		return dialog;
	}
	
	@Override
	public void onCancel(DialogInterface dialog){
		super.onCancel(dialog);
		getActivity().finish();
	}
}
