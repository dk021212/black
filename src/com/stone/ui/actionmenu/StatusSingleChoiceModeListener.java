package com.stone.ui.actionmenu;

import com.stone.bean.MessageBean;
import com.stone.black.R;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;
import com.stone.ui.basefragment.AbstractTimeLineFragment;
import com.stone.ui.browser.BrowserWeiboMsgFragment;
import com.stone.ui.send.WriteCommentActivity;
import com.stone.ui.send.WriteRepostActivity;
import com.stone.ui.task.FavAsyncTask;
import com.stone.ui.task.UnFavAsyncTask;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ShareActionProvider;
import android.widget.Toast;

public class StatusSingleChoiceModeListener implements ActionMode.Callback {

	private ListView listView;
	private BaseAdapter adapter;
	private Fragment fragment;
	private ActionMode mode;
	private MessageBean bean;
	private ShareActionProvider mShareActionProvider;

	private FavAsyncTask favTask = null;
	private UnFavAsyncTask unFavTask = null;

	public void finish() {
		if (mode != null) {
			mode.finish();
		}
	}

	public StatusSingleChoiceModeListener(ListView listView,
			BaseAdapter adapter, Fragment fragment, MessageBean bean) {
		this.listView = listView;
		this.fragment = fragment;
		this.adapter = adapter;
		this.bean = bean;
	}

	private Activity getActivity() {
		return fragment.getActivity();
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		MenuInflater inflater = mode.getMenuInflater();
		menu.clear();

		if (bean.getUser().getId()
				.equals(GlobalContext.getInstance().getCurrentAccountId())) {
			inflater.inflate(
					R.menu.contextual_menu_fragment_status_listview_myself,
					menu);
		} else {
			inflater.inflate(R.menu.contextual_menu_fragment_status_listview,
					menu);
		}

		mode.setTitle(bean.getUser().getScreen_name());

		MenuItem item = menu.findItem(R.id.menu_share);
		mShareActionProvider = (ShareActionProvider) item.getActionProvider();
		if (fragment.getActivity() != null) {
			Utility.setShareIntent(fragment.getActivity(),
					mShareActionProvider, bean);
		}

		mShareActionProvider
				.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {

					@Override
					public boolean onShareTargetSelected(
							ShareActionProvider source, Intent intent) {
						finish();
						return false;
					}
				});

		return true;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		if (this.mode == null) {
			this.mode = mode;
		}

		return true;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		 if (listView.getCheckedItemCount() == 0) {
	            return true;
	        }

	        Intent intent;
	        long[] ids = listView.getCheckedItemIds();
	        switch (item.getItemId()) {
	            case R.id.menu_repost:
	                intent = new Intent(getActivity(), WriteRepostActivity.class);
	                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
	                intent.putExtra("id", String.valueOf(ids[0]));
	                intent.putExtra("msg", bean);
	                getActivity().startActivity(intent);
	                listView.clearChoices();
	                mode.finish();
	                break;
	            case R.id.menu_comment:
	                intent = new Intent(getActivity(), WriteCommentActivity.class);
	                intent.putExtra("token", GlobalContext.getInstance().getSpecialToken());
	                intent.putExtra("id", String.valueOf(ids[0]));
	                intent.putExtra("msg", bean);
	                getActivity().startActivity(intent);
	                listView.clearChoices();
	                mode.finish();

	                break;
	            case R.id.menu_fav:
	                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
	                    favTask = new FavAsyncTask(GlobalContext.getInstance().getSpecialToken(), bean.getId());
	                    favTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
	                }
	                listView.clearChoices();
	                mode.finish();
	                break;
	            case R.id.menu_unfav:
	                if (Utility.isTaskStopped(favTask) && Utility.isTaskStopped(unFavTask)) {
	                    unFavTask = new UnFavAsyncTask(GlobalContext.getInstance().getSpecialToken(), bean.getId());
	                    unFavTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
	                }
	                listView.clearChoices();
	                mode.finish();
	                break;
	            case R.id.menu_remove:

	                int position = listView.getCheckedItemPosition() - listView.getHeaderViewsCount();
	                RemoveDialog dialog = new RemoveDialog(position);
	                dialog.setTargetFragment(fragment, 0);
	                dialog.show(fragment.getFragmentManager(), "");

	                break;
	            case R.id.menu_share:
	                if (fragment.getActivity() != null)
	                    Utility.setShareIntent(fragment.getActivity(), mShareActionProvider, bean);
	                mShareActionProvider.setOnShareTargetSelectedListener(new ShareActionProvider.OnShareTargetSelectedListener() {
	                    @Override
	                    public boolean onShareTargetSelected(ShareActionProvider source, Intent intent) {
	                        finish();
	                        return false;
	                    }
	                });
	                break;
	            case R.id.menu_copy:
	                ClipboardManager cm = (ClipboardManager) fragment.getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
	                cm.setPrimaryClip(ClipData.newPlainText("sinaweibo", bean.getText()));
	                Toast.makeText(fragment.getActivity(), fragment.getString(R.string.copy_successfully), Toast.LENGTH_SHORT).show();
	                mode.finish();
	                break;
	        }

	        return true;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		this.mode = null;
        listView.clearChoices();
        adapter.notifyDataSetChanged();
        if (fragment instanceof AbstractTimeLineFragment)
            ((AbstractTimeLineFragment) fragment).setmActionMode(null);

        if (fragment instanceof BrowserWeiboMsgFragment)
            ((BrowserWeiboMsgFragment) fragment).setmActionMode(null);
	}

}
