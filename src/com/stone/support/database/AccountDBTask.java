package com.stone.support.database;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.stone.bean.AccountBean;
import com.stone.bean.UserBean;
import com.stone.support.database.table.AccountTable;
import com.stone.support.debug.AppLogger;
import com.stone.ui.login.OAuthActivity;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AccountDBTask {

	private AccountDBTask() {

	}

	private static SQLiteDatabase getWsd() {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getWritableDatabase();
	}

	private static SQLiteDatabase getRsd() {
		DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
		return databaseHelper.getReadableDatabase();
	}

	public static List<AccountBean> getAccountList() {
		List<AccountBean> accountList = new ArrayList<AccountBean>();
		String sql = "select * from " + AccountTable.TABLE_NAME;
		Cursor c = getWsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			AccountBean account = new AccountBean();
			int colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN);
			account.setAccess_token(c.getString(colid));

			colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN_EXPIRES_TIME);
			account.setExpires_time(Long.valueOf(c.getString(colid)));

			colid = c.getColumnIndex(AccountTable.BLACK_MAGIC);
			account.setBlack_magic(c.getInt(colid) == 1);

			colid = c.getColumnIndex(AccountTable.NAVIGATION_POSITION);
			account.setNavigationPosition(c.getInt(colid));

			Gson gson = new Gson();
			String json = c.getString(c.getColumnIndex(AccountTable.INFOJSON));
			try {
				UserBean value = gson.fromJson(json, UserBean.class);
				account.setInfo(value);
			} catch (JsonSyntaxException e) {
				AppLogger.e(e.getMessage());
			}

			accountList.add(account);
		}
		c.close();
		return accountList;
	}

	public static AccountBean getAccount(String id) {
		String sql = "select * from " + AccountTable.TABLE_NAME + " where "
				+ AccountTable.UID + " = " + id;
		Cursor c = getRsd().rawQuery(sql, null);
		if (c.moveToNext()) {
			AccountBean account = new AccountBean();
			int colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN);
			account.setAccess_token(c.getString(colid));

			colid = c.getColumnIndex(AccountTable.OAUTH_TOKEN_EXPIRES_TIME);
			account.setExpires_time(Long.valueOf(c.getString(colid)));

			colid = c.getColumnIndex(AccountTable.BLACK_MAGIC);
			account.setBlack_magic(c.getInt(colid) == 1);

			colid = c.getColumnIndex(AccountTable.NAVIGATION_POSITION);
			account.setNavigationPosition(c.getInt(colid));

			Gson gson = new Gson();
			String json = c.getString(c.getColumnIndex(AccountTable.INFOJSON));
			try {
				UserBean value = gson.fromJson(json, UserBean.class);
				account.setInfo(value);
			} catch (JsonSyntaxException e) {
				AppLogger.e(e.getMessage());
			}
			return account;
		}
		return null;
	}

	public static OAuthActivity.DBResult addOrUpdateAccount(
			AccountBean account, boolean blackMagic) {
		ContentValues cv = new ContentValues();
		cv.put(AccountTable.UID, account.getUid());
		cv.put(AccountTable.OAUTH_TOKEN, account.getAccess_token());
		cv.put(AccountTable.OAUTH_TOKEN_EXPIRES_TIME,
				String.valueOf(account.getExpires_time()));
		cv.put(AccountTable.BLACK_MAGIC, blackMagic);

		String json = new Gson().toJson(account.getInfo());
		cv.put(AccountTable.INFOJSON, json);

		Cursor c = getWsd().query(AccountTable.TABLE_NAME, null,
				AccountTable.UID + "=?", new String[] { account.getUid() },
				null, null, null);

		if (c != null && c.getCount() > 0) {
			String[] args = { account.getUid() };
			getWsd().update(AccountTable.TABLE_NAME, cv,
					AccountTable.UID + "=?", args);
			return OAuthActivity.DBResult.update_successfully;
		} else {
			getWsd().insert(AccountTable.TABLE_NAME, AccountTable.UID, cv);
			return OAuthActivity.DBResult.add_successfully;
		}
	}
}
