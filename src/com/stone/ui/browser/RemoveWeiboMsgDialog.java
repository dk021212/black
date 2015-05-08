package com.stone.ui.browser;

import com.stone.black.R;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class RemoveWeiboMsgDialog extends DialogFragment {

	public static interface IRemove {
		public void removeMsg(String id);
	}

	private String id;

	public RemoveWeiboMsgDialog() {

	}

	public RemoveWeiboMsgDialog(String id) {

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

		outState.putString("id", id);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			id = savedInstanceState.getString("id");
		}
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setTitle(getString(R.string.askdelete))
				.setMessage(getString(R.string.askdeletemessage))
				.setPositiveButton(getString(R.string.delete),
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								IRemove IRemove = (IRemove) getActivity();
								IRemove.removeMsg(id);
							}
						})
				.setNegativeButton(getString(R.string.cancel),
						new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});

		return builder.create();
	}

}
