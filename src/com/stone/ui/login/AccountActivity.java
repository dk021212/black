package com.stone.ui.login;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.crashlytics.android.Crashlytics;
import com.stone.black.R;
import com.stone.ui.interfaces.AbstractAppActivity;

import io.fabric.sdk.android.Fabric;

public class AccountActivity extends AbstractAppActivity {
	
	private final int ADD_ACCOUNT_REQUEST_CODE=0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.account_activity_layout);
		getActionBar().setTitle(getString(R.string.app_name));
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		getMenuInflater().inflate(R.menu.actionbar_menu_accountactivity, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item){
		return true;
	}

}
