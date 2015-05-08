package com.stone.ui.browser;

import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.widget.ShareActionProvider;
import android.widget.Toast;

import com.stone.bean.MessageBean;
import com.stone.dao.destroy.DestroyStatusDao;
import com.stone.support.error.WeiboException;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.utils.GlobalContext;
import com.stone.ui.interfaces.AbstractAppActivity;
import com.stone.ui.task.FavAsyncTask;
import com.stone.ui.task.UnFavAsyncTask;

public class BrowserWeiboMsgActivity extends AbstractAppActivity implements
		RemoveWeiboMsgDialog.IRemove {

	private static final String ACTION_WITH_ID = "action_with_id";
	private static final String ACTION_WITH_DETAIL = "action_with_detail";
	private static final int REFRESH_LOADER_ID = 0;

	private MessageBean msg;
	private String msgId;
	private String token;
	private FavAsyncTask favTask = null;
	private UnFavAsyncTask unFavTask = null;
	private ShareActionProvider shareActionProvider;
	private GestureDetector gestureDetector;
	private RemoveTask removeTask;

	public static Intent newIntent(String weiboId, String token) {
		Intent intent = new Intent(GlobalContext.getInstance(),
				BrowserWeiboMsgActivity.class);
		intent.putExtra("weiboId", weiboId);
		intent.putExtra("token", token);
		intent.setAction(ACTION_WITH_ID);
		return intent;
	}

	public static Intent newIntent(MessageBean msg, String token) {
		Intent intent = new Intent(GlobalContext.getInstance(),
				BrowserWeiboMsgActivity.class);
		intent.putExtra("msg", msg);
		intent.putExtra("token", token);
		intent.setAction(ACTION_WITH_DETAIL);
		return intent;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putParcelable("msg", msg);
		outState.putString("token", token);
	}

	@Override
	public void removeMsg(String id) {
		// TODO Auto-generated method stub

	}

	class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

		String id;
		WeiboException e;

		public RemoveTask(String id) {
			this.id = id;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			DestroyStatusDao dao = new DestroyStatusDao(token, id);
			try {
				return dao.destroy();
			} catch (WeiboException e) {
				this.e = e;
				cancel(true);
				return false;
			}
		}

		@Override
		protected void onCancelled(Boolean aBoolean) {
			super.onCancelled(aBoolean);
			if (this.e != null) {
				Toast.makeText(BrowserWeiboMsgActivity.this, e.getError(),
						Toast.LENGTH_SHORT).show();
			}
		}

	}

}
