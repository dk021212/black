package com.stone.ui.login;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.stone.black.R;
import com.stone.ui.interfaces.AbstractAppActivity;

import io.fabric.sdk.android.Fabric;

public class AccountActivity extends AbstractAppActivity {

	private final int ADD_ACCOUNT_REQUEST_CODE = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.account_activity_layout);
		getActionBar().setTitle(getString(R.string.app_name));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.actionbar_menu_accountactivity, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_add_account:
			final ArrayList<Class> activityList = new ArrayList<Class>();
			ArrayList<String> itemValueList = new ArrayList<String>();

			activityList.add(OAuthActivity.class);
			itemValueList.add(getString(R.string.oauth_login));

			new AlertDialog.Builder(this).setItems(
					itemValueList.toArray(new String[0]),
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent(AccountActivity.this,
									activityList.get(which));
							startActivityForResult(intent,
									ADD_ACCOUNT_REQUEST_CODE);

						}
					}).show();
			break;
		}
		return true;
	}

}
