package com.stone.ui.login;

import android.os.Bundle;
import android.view.Menu;

import com.stone.black.R;
import com.stone.ui.interfaces.AbstractAppActivity;

public class AccountActivity extends AbstractAppActivity {
	
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

}
