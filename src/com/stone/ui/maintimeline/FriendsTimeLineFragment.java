package com.stone.ui.maintimeline;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.stone.bean.AccountBean;
import com.stone.bean.GroupBean;
import com.stone.bean.MessageListBean;
import com.stone.bean.UserBean;
import com.stone.bean.android.MessageTimeLineData;
import com.stone.bean.android.TimeLinePosition;
import com.stone.ui.basefragment.AbstractMessageTimeLineFragment;
import com.stone.support.database.FriendsTimeLineDBTask;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.utils.GlobalContext;
import com.stone.ui.main.MainTimeLineActivity;

public class FriendsTimeLineFragment extends
		AbstractMessageTimeLineFragment<MessageListBean> implements
		GlobalContext.MyProfileInfoChangeListener,
		MainTimeLineActivity.ScrollableListFragment {

	private AccountBean accountBean;
	private UserBean userBean;
	private String token;

	private DBCacheTask dbTask;

	private ScheduledExecutorService autoRefreshExecutor = null;

	public final static String ALL_GROUP_ID = "0";
	public final static String BILATERAL_GROUP_ID = "1";

	private String currentGroupId = ALL_GROUP_ID;

	private HashMap<String, MessageListBean> groupDataCache = new HashMap<String, MessageListBean>();
	private HashMap<String, TimeLinePosition> positionCache = new HashMap<String, TimeLinePosition>();

	private MessageListBean bean = new MessageListBean();

	private BaseAdapter navAdapter;

	private Thread backgroundWifiDownloadPicThread = null;

	public static FriendsTimeLineFragment newInstance(AccountBean accountBean,
			UserBean userBean, String token) {
		FriendsTimeLineFragment fragment = new FriendsTimeLineFragment(
				accountBean, userBean, token);
		fragment.setArguments(new Bundle());
		return fragment;
	}

	public FriendsTimeLineFragment(AccountBean accountBean, UserBean userBean,
			String token) {
		this.accountBean = accountBean;
		this.userBean = userBean;
		this.token = token;
	}

	public void buildActionBarNav() {
		// TODO Auto-generated method stub

	}

	@Override
	public MessageListBean getList() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void listViewItemClick(AdapterView parent, View view,
			int position, long id) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void newMsgOnPostExecute(MessageListBean newValue,
			Bundle loaderArgs) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void oldMsgOnPostExecute(MessageListBean newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void buildListAdapter() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChange(UserBean newUserBean) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scrollToTop() {
		// TODO Auto-generated method stub

	}

	public void handleDBCacheOnProgressUpdateData(MessageTimeLineData[] result) {
		if (result != null && result.length > 0) {
			MessageTimeLineData recentData = result[0];
			getList().replaceData(recentData.msgList);
			putToGroupDataMemoryCache(recentData.groupId, recentData.msgList);
			positionCache.put(recentData.groupId, recentData.position);
			currentGroupId = recentData.groupId;
		}

		getPullToRefreshListView().setVisibility(View.VISIBLE);
		getAdapter().notifyDataSetChanged();
		setListViewPositionFromPositionsCache();
		if (getActivity().getActionBar().getNavigationMode() == ActionBar.NAVIGATION_MODE_LIST) {
			getActivity().getActionBar().setSelectedNavigationItem(
					getRecentNavIndex());
		}

	}
	
	private void putToGroupDataMemoryCache(String groupId, MessageListBean value) {
        MessageListBean copy = new MessageListBean();
        copy.addNewData(value);
        groupDataCache.put(groupId, copy);
    }
	
	private void setListViewPositionFromPositionsCache() {
        TimeLinePosition p = positionCache.get(currentGroupId);
        if (p != null) {
            getListView().setSelectionFromTop(p.position + 1, p.top);
        } else {
            getListView().setSelectionFromTop(0, 0);
        }

        setListViewUnreadTipBar(p);

    }

	private int getIndexFromGroupId(String id, List<GroupBean> list) {

		if (list == null || list.size() == 0) {
			return 0;
		}

		int index = 0;

		if (id.equals("0")) {
			index = 0;
		} else if (id.equals("1")) {
			index = 1;
		}

		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getIdstr().equals(id)) {
				index = i + 2;
				break;
			}
		}
		return index;
	}
	
	private int getRecentNavIndex() {
        List<GroupBean> list = new ArrayList<GroupBean>();
        if (GlobalContext.getInstance().getGroup() != null) {
            list = GlobalContext.getInstance().getGroup().getLists();
        } else {
            list = new ArrayList<GroupBean>();
        }
        return getIndexFromGroupId(currentGroupId, list);
    }

	private static class DBCacheTask extends
			MyAsyncTask<Void, MessageTimeLineData, List<MessageTimeLineData>> {

		private WeakReference<FriendsTimeLineFragment> fragmentWeakReference;
		private String accountId;

		private DBCacheTask(FriendsTimeLineFragment friendsTimeLineFragment,
				String accountId) {
			fragmentWeakReference = new WeakReference<FriendsTimeLineFragment>(
					friendsTimeLineFragment);
			this.accountId = accountId;
		}

		@Override
		protected List<MessageTimeLineData> doInBackground(Void... params) {
			MessageTimeLineData recentGroupData = FriendsTimeLineDBTask
					.getRecentGroupData(accountId);
			publishProgress(recentGroupData);
			return FriendsTimeLineDBTask.getOtherGroupData(accountId,
					recentGroupData.groupId);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			FriendsTimeLineFragment fragment = fragmentWeakReference.get();
			if (fragment != null) {
				fragment.getPullToRefreshListView().setVisibility(
						View.INVISIBLE);
			}
		}

		@Override
		protected void onPostExecute(List<MessageTimeLineData> result) {
			super.onPostExecute(result);
			FriendsTimeLineFragment fragment = fragmentWeakReference.get();

			if (fragment == null) {
				return;
			}

			if (fragment.getActivity() == null) {
				return;
			}

			if (result != null && result.size() > 0) {
				fragment.handleDBCacheResultData(result);
			}
		}

		@Override
		protected void onProgressUpdate(MessageTimeLineData... result) {
			super.onProgressUpdate(result);

			FriendsTimeLineFragment fragment = fragmentWeakReference.get();

			if (fragment == null) {
				return;
			}

			if (fragment.getActivity() == null) {
				return;
			}

			fragment.handleDBCacheOnProgressUpdateData(result);
		}

	}

	public void handleDBCacheResultData(List<MessageTimeLineData> result) {
		// TODO Auto-generated method stub

	}

}
