package com.stone.ui.login;

//import com.crashlytics.android.Crashlytics;
//import io.fabric.sdk.android.Fabric;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.stone.bean.AccountBean;
import com.stone.black.R;
import com.stone.support.database.AccountDBTask;
import com.stone.support.debug.AppLogger;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;
import com.stone.ui.interfaces.AbstractAppActivity;
import com.stone.ui.main.MainTimeLineActivity;

public class AccountActivity extends AbstractAppActivity implements
		LoaderManager.LoaderCallbacks<List<AccountBean>> {

	private final int ADD_ACCOUNT_REQUEST_CODE = 0;
	private static final String ACTION_OPEN_FROM_APP_INNER = "com.stone:accountactivity";
	private final int LOADER_ID = 0;
	private List<AccountBean> accountList = new ArrayList<AccountBean>();
	private ListView listView = null;
	private AccountAdapter listAdapter = null;

	public static Intent newItent() {
		Intent intent = new Intent(GlobalContext.getInstance(),
				AccountActivity.class);
		intent.setAction(ACTION_OPEN_FROM_APP_INNER);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		if (getIntent() != null
				&& !ACTION_OPEN_FROM_APP_INNER.equals(getIntent().getAction())) {
			jumpToMainTimeLineActivity();
		}

		super.onCreate(savedInstanceState);
//		Fabric.with(this, new Crashlytics());

		setContentView(R.layout.account_activity_layout);
		getActionBar().setTitle(getString(R.string.app_name));
		listAdapter = new AccountAdapter();
		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new AccountListItemClickListener());
		listView.setAdapter(listAdapter);
		listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new AccountMultiChoiceModeListener());
		getSupportLoaderManager().initLoader(LOADER_ID, null, this);

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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == ADD_ACCOUNT_REQUEST_CODE && resultCode == RESULT_OK) {
			refresh();
			if (data == null) {
				return;
			}

			String expires_time = data.getExtras().getString("expires_in");
			long expiresDays = TimeUnit.SECONDS.toDays(Long
					.valueOf(expires_time));
			String content = String.format(
					getString(R.string.token_expires_in_time),
					String.valueOf(expiresDays));
			AlertDialog.Builder builder = new AlertDialog.Builder(this)
					.setMessage(content).setPositiveButton(R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {

								}
							});
			builder.show();
		}
	}

	private void jumpToMainTimeLineActivity() {
		String id = SettingUtility.getDefaultAccountId();

		if (!TextUtils.isEmpty(id)) {
			AccountBean bean = AccountDBTask.getAccount(id);
			if (bean != null) {
				Intent start = MainTimeLineActivity.newIntent(bean);
				startActivity(start);
				finish();
			}
		}
	}

	private void refresh() {
		getSupportLoaderManager().getLoader(LOADER_ID).forceLoad();
	}

	@Override
	public Loader<List<AccountBean>> onCreateLoader(int id, Bundle args) {
		return new AccountDBLoader(AccountActivity.this, args);
	}

	@Override
	public void onLoadFinished(Loader<List<AccountBean>> loader,
			List<AccountBean> data) {
		accountList = data;
		listAdapter.notifyDataSetChanged();
	}

	@Override
	public void onLoaderReset(Loader<List<AccountBean>> loader) {
		accountList = new ArrayList<AccountBean>();
		listAdapter.notifyDataSetChanged();

	}

	private class AccountListItemClickListener implements
			AdapterView.OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent = MainTimeLineActivity.newIntent(accountList
					.get(position));
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
		}

	}

	private class AccountMultiChoiceModeListener implements
			AbsListView.MultiChoiceModeListener {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			mode.getMenuInflater().inflate(
					R.menu.contextual_menu_accountactivity, menu);
			mode.setTitle(getString(R.string.account_management));
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch (item.getItemId()) {
			case R.id.menu_remove_accouont:
				remove();
				mode.finish();
				return true;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {

		}

		@Override
		public void onItemCheckedStateChanged(ActionMode mode, int position,
				long id, boolean checked) {
			listAdapter.notifyDataSetChanged();
		}

	}

	private class AccountAdapter extends BaseAdapter {

		int checkedBG;
		int defaultBG;

		public AccountAdapter() {
			defaultBG = getResources().getColor(R.color.transparent);

			int[] attrs = new int[] { R.attr.listview_checked_color };
			TypedArray ta = obtainStyledAttributes(attrs);
			checkedBG = ta.getColor(0, 430);
		}

		@Override
		public int getCount() {
			return accountList.size();
		}

		@Override
		public Object getItem(int position) {
			return accountList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return Long.valueOf(accountList.get(position).getUid());
		}

		@Override
		public boolean hasStableIds() {
			return true;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater layoutInflater = getLayoutInflater();

			View mView = layoutInflater.inflate(
					R.layout.accountactivity_listview_item_layout, parent,
					false);
			mView.findViewById(R.id.listview_root)
					.setBackgroundColor(defaultBG);

			if (listView.getCheckedItemPositions().get(position)) {
				mView.findViewById(R.id.listview_root).setBackgroundColor(
						checkedBG);
			}

			TextView textView = (TextView) mView
					.findViewById(R.id.account_name);
			if (accountList.get(position).getInfo() != null) {
				textView.setText(accountList.get(position).getInfo()
						.getScreen_name());
			} else {
				textView.setText(accountList.get(position).getUsernick());
			}

			ImageView imageView = (ImageView) mView
					.findViewById(R.id.imageView_avatar);

			if (!TextUtils.isEmpty(accountList.get(position).getAvatar_url())) {
				AppLogger.i("avatar available");

				getBitmapDownloader().downloadAvatar(imageView,
						accountList.get(position).getInfo(), false);
			} else {
				AppLogger.i("no avatar");
			}

			TextView token = (TextView) mView.findViewById(R.id.token_expired);
			if (!Utility.isTokenValid(accountList.get(position))) {
				token.setVisibility(View.VISIBLE);
			} else {
				token.setVisibility(View.GONE);
			}

			return mView;
		}

	}

	private static class AccountDBLoader extends
			AsyncTaskLoader<List<AccountBean>> {

		public AccountDBLoader(Context context, Bundle args) {
			super(context);
		}

		@Override
		protected void onStartLoading() {
			super.onStartLoading();
			forceLoad();
		}

		public List<AccountBean> loadInBackground() {
			return AccountDBTask.getAccountList();
		}
	}

	private void remove() {
		Set<String> set = new HashSet<String>();
		long[] ids = listView.getCheckItemIds();
		for (long id : ids) {
			set.add(String.valueOf(id));
		}
		accountList = AccountDBTask.removeAndGetNewAccountList(set);
		listAdapter.notifyDataSetChanged();
	}

}
