package com.stone.ui.main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeSet;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.slidingmenu.lib.SlidingMenu;
import com.stone.bean.AccountBean;
import com.stone.bean.android.TimeLinePosition;
import com.stone.black.R;
import com.stone.support.asyncdrawable.TimeLineBitmapDownloader;
import com.stone.support.database.AccountDBTask;
import com.stone.support.database.CommentToMeTimeLineDBTask;
import com.stone.support.database.MentionCommentsTimeLineDBTask;
import com.stone.support.database.MentionWeiboTimeLineDBTask;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.utils.AppEventAction;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;
import com.stone.ui.interfaces.AbstractAppFragment;
import com.stone.ui.login.AccountActivity;
import com.stone.ui.maintimeline.FriendsTimeLineFragment;
import com.stone.ui.preference.SettingActivity;

public class LeftMenuFragment extends AbstractAppFragment {

	private Layout layout;

	private int currentIndex = -1;

	private int mentionsWeiboUnreadCount = 0;

	private int mentionsCommentUnreadCount = 0;

	private int commentsToMeUnreadCount = 0;

	public int commentsTabIndex = -1;

	public int mentionsTabIndex = -1;

	public int searchTabIndex = -1;

	private boolean firstStart = true;

	private SparseArray<Fragment> rightFragments = new SparseArray<Fragment>();

	public static final int HOME_INDEX = 0;

	public static final int MENTIONS_INDEX = 1;

	public static final int COMMENTS_INDEX = 2;

	public static final int DM_INDEX = 3;

	public static final int FAV_INDEX = 4;

	public static final int SEARCH_INDEX = 5;

	public static final int PROFILE_INDEX = 6;

	public static final int LOGOUT_INDEX = 7;

	public static final int SETTING_INDEX = 8;

	public static LeftMenuFragment newInstance() {
		LeftMenuFragment fragment = new LeftMenuFragment();
		fragment.setArguments(new Bundle());
		return fragment;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt("currentIndex", currentIndex);
		outState.putInt("mentionsWeiboUnreadCount", mentionsWeiboUnreadCount);
		outState.putInt("mentionsCommentUnreadCount",
				mentionsCommentUnreadCount);
		outState.putInt("commentsToMeUnreadCount", commentsToMeUnreadCount);
		outState.putInt("commentsTabIndex", commentsTabIndex);
		outState.putInt("mentionsTabIndex", mentionsTabIndex);
		outState.putInt("searchTabIndex", searchTabIndex);
		outState.putBoolean("firstStart", firstStart);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (savedInstanceState != null) {
			currentIndex = savedInstanceState.getInt("currentIndex");
			mentionsWeiboUnreadCount = savedInstanceState
					.getInt("mentionsWeiboUnreadCount");
			mentionsCommentUnreadCount = savedInstanceState
					.getInt("mentionsCommentUnreadCount");
			commentsToMeUnreadCount = savedInstanceState
					.getInt("commentsToMeUnreadCount");
			commentsTabIndex = savedInstanceState.getInt("commentsTabIndex");
			mentionsTabIndex = savedInstanceState.getInt("mentionsTabIndex");
			searchTabIndex = savedInstanceState.getInt("searchTabIndex");
			firstStart = savedInstanceState.getBoolean("firstStart");
		} else {
			readUnreadCountFromDB();
		}

		if (currentIndex == -1) {
			currentIndex = GlobalContext.getInstance().getAccountBean()
					.getNavigationPosition() / 10;
		}

		rightFragments.append(HOME_INDEX,
				((MainTimeLineActivity) getActivity())
						.getFriendsTimeLineFragment());
		rightFragments.append(MENTIONS_INDEX,
				((MainTimeLineActivity) getActivity())
						.getMentionsTimeLineFragment());
		rightFragments.append(COMMENTS_INDEX,
				((MainTimeLineActivity) getActivity())
						.getCommentsTimeLineFragment());
		rightFragments.append(SEARCH_INDEX,
				((MainTimeLineActivity) getActivity()).getSearchFragment());
		rightFragments.append(DM_INDEX,
				((MainTimeLineActivity) getActivity()).getDMFragment());
		rightFragments.append(FAV_INDEX,
				((MainTimeLineActivity) getActivity()).getFavFragment());
		rightFragments.append(PROFILE_INDEX,
				((MainTimeLineActivity) getActivity()).getMyProfileFragment());

		switchCategory(currentIndex);

		layout.nickname.setText(GlobalContext.getInstance()
				.getCurrentAccountName());
		layout.avatar.setAdapter(new AvatarAdapter(layout.avatar));
	}

	public void switchCategory(int position) {

		switch (position) {
		case HOME_INDEX:
			showHomePage(true);
			break;
		case MENTIONS_INDEX:
			showMentionPage(true);
			break;
		case COMMENTS_INDEX:
			showCommentPage(true);
			break;
		case SEARCH_INDEX:
			showSearchPage(true);
			break;
		case DM_INDEX:
			showDMPage(true);
			break;
		case FAV_INDEX:
			showFavPage(true);
			break;
		case PROFILE_INDEX:
			showProfilePage(true);
			break;
		}
		drawButtonsBackground(position);

		buildUnreadCount();

		firstStart = false;
	}

	private void drawButtonsBackground(int position) {
		
		layout.home.setBackgroundResource(R.drawable.btn_drawer_menu);
		layout.mention.setBackgroundResource(R.drawable.btn_drawer_menu);
		layout.comment.setBackgroundResource(R.drawable.btn_drawer_menu);
		layout.search.setBackgroundResource(R.drawable.btn_drawer_menu);
		layout.profile.setBackgroundResource(R.drawable.btn_drawer_menu);
		layout.dm.setBackgroundResource(R.drawable.btn_drawer_menu);
		layout.fav.setBackgroundResource(R.drawable.btn_drawer_menu);

		switch (position) {
		case HOME_INDEX:
			layout.home.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case MENTIONS_INDEX:
			layout.mention.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case COMMENTS_INDEX:
			layout.comment.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case SEARCH_INDEX:
			layout.search.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case DM_INDEX:
			layout.dm.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case FAV_INDEX:
			layout.fav.setBackgroundResource(R.color.ics_blue_semi);
			break;
		// case 5:
		// layout.location.setBackgroundResource(R.color.ics_blue_semi);
		// break;
		case PROFILE_INDEX:
			layout.profile.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case LOGOUT_INDEX:
			layout.logout.setBackgroundResource(R.color.ics_blue_semi);
			break;
		case SETTING_INDEX:
			layout.setting.setBackgroundResource(R.color.ics_blue_semi);
			break;
		}
	}

	private boolean showHomePage(boolean reset) {
		if (currentIndex == HOME_INDEX && !reset) {
			((MainTimeLineActivity) getActivity()).getSlidingMenu()
					.showContent();
			return true;
		}
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
		currentIndex = HOME_INDEX;

		if (Utility.isDevicePort() && !reset) {
			BroadcastReceiver receiver = new BroadcastReceiver() {

				@Override
				public void onReceive(Context context, Intent intent) {
					LocalBroadcastManager.getInstance(getActivity())
							.unregisterReceiver(this);
					if (currentIndex == HOME_INDEX) {
						showHomePageImp();
					}
				}

			};
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
					receiver,
					new IntentFilter(
							AppEventAction.SLIDING_MENU_CLOSED_BROADCASST));
		} else {
			showHomePageImp();
		}

		((MainTimeLineActivity) getActivity()).getSlidingMenu().showContent();

		return false;
	}

	private void showHomePageImp() {
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.hide(rightFragments.get(MENTIONS_INDEX));
		ft.hide(rightFragments.get(COMMENTS_INDEX));
		ft.hide(rightFragments.get(SEARCH_INDEX));
		ft.hide(rightFragments.get(DM_INDEX));
		ft.hide(rightFragments.get(FAV_INDEX));
		ft.hide(rightFragments.get(PROFILE_INDEX));

		FriendsTimeLineFragment fragment = (FriendsTimeLineFragment) rightFragments
				.get(HOME_INDEX);
		ft.show(fragment);
		ft.commit();
		setTitle("");
		fragment.buildActionBarNav();

	}

	private void showProfilePage(boolean b) {
		// TODO Auto-generated method stub

	}

	private void showFavPage(boolean b) {
		// TODO Auto-generated method stub

	}

	private void showDMPage(boolean b) {
		// TODO Auto-generated method stub

	}

	private void showSearchPage(boolean b) {
		// TODO Auto-generated method stub

	}

	private void showCommentPage(boolean b) {
		// TODO Auto-generated method stub

	}

	private void showMentionPage(boolean b) {
		// TODO Auto-generated method stub

	}

	private void readUnreadCountFromDB() {
		TimeLinePosition position = MentionWeiboTimeLineDBTask
				.getPosition(GlobalContext.getInstance().getCurrentAccountId());
		TreeSet<Long> hashSet = position.newMsgIds;
		if (hashSet != null) {
			mentionsWeiboUnreadCount = hashSet.size();
		}

		position = MentionCommentsTimeLineDBTask.getPosition(GlobalContext
				.getInstance().getCurrentAccountId());
		hashSet = position.newMsgIds;
		if (hashSet != null) {
			mentionsCommentUnreadCount = hashSet.size();
		}
		position = CommentToMeTimeLineDBTask.getPosition(GlobalContext
				.getInstance().getCurrentAccountId());
		hashSet = position.newMsgIds;
		if (hashSet != null) {
			commentsToMeUnreadCount = hashSet.size();
		}
	}

	private void buildUnreadCount() {
		setMentionWeiboUnreadCount(mentionsWeiboUnreadCount);
		setMentionCommentUnreadCount(mentionsCommentUnreadCount);
		setCommentUnreadCount(commentsToMeUnreadCount);
	}

	private void showAccountSwitchPage() {
		Intent intent = AccountActivity.newItent();
		startActivity(intent);
		getActivity().finish();
	}

	private void showSettingPage() {
		startActivity(new Intent(getActivity(), SettingActivity.class));
	}

	public int getCurrentIndex() {
		return currentIndex;
	}

	public void setMentionWeiboUnreadCount(int count) {
		this.mentionsCommentUnreadCount = count;
		int totalCount = this.mentionsWeiboUnreadCount
				+ this.mentionsCommentUnreadCount;
		if (totalCount > 0) {
			layout.mentionCount.setVisibility(View.VISIBLE);
			layout.mentionCount.setText(String.valueOf(totalCount));
		} else {
			layout.mentionCount.setVisibility(View.GONE);
		}
	}

	public void setMentionCommentUnreadCount(int count) {
		this.mentionsCommentUnreadCount = count;
		int totalCount = this.mentionsWeiboUnreadCount
				+ this.mentionsCommentUnreadCount;
		if (totalCount > 0) {
			layout.mentionCount.setVisibility(View.VISIBLE);
			layout.mentionCount.setText(String.valueOf(totalCount));
		} else {
			layout.mentionCount.setVisibility(View.GONE);
		}
	}

	public void setCommentUnreadCount(int count) {
		this.commentsToMeUnreadCount = count;
		if (this.commentsToMeUnreadCount > 0) {
			layout.commentCount.setVisibility(View.VISIBLE);
			layout.commentCount.setText(String
					.valueOf(this.commentsToMeUnreadCount));
		} else {
			layout.commentCount.setVisibility(View.GONE);
		}
	}

	private class AvatarAdapter extends BaseAdapter {

		ArrayList<AccountBean> data = new ArrayList<AccountBean>();

		int count = 0;

		public AvatarAdapter(Spinner spinner) {
			data.addAll(AccountDBTask.getAccountList());
			if (data.size() == 1) {
				count = 1;
			} else {
				count = data.size() - 1;
			}

			Iterator<AccountBean> iterator = data.iterator();
			while (iterator.hasNext()) {
				AccountBean accountBean = iterator.next();
				if (accountBean.getUid().equals(
						GlobalContext.getInstance().getAccountBean().getUid())) {
					iterator.remove();
					break;
				}
			}
		}

		@Override
		public int getCount() {
			return count;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = getLayoutInflater(null).inflate(
					R.layout.slidingdrawer_avatar, parent, false);
			ImageView iv = (ImageView) view.findViewById(R.id.avatar);
			TimeLineBitmapDownloader.getInstance()
					.display(
							iv,
							-1,
							-1,
							GlobalContext.getInstance().getAccountBean()
									.getInfo().getAvatar_large(),
							FileLocationMethod.avatar_large);

			return view;
		}

		@Override
		public View getDropDownView(int position, View convertView,
				ViewGroup parent) {
			View view = getLayoutInflater(null).inflate(
					R.layout.slidingdrawer_avatar_dropdown, parent, false);
			TextView nickname = (TextView) view.findViewById(R.id.nickname);
			ImageView avatar = (ImageView) view.findViewById(R.id.avatar);

			if (data.size() > 0) {
				final AccountBean accountBean = data.get(position);
				TimeLineBitmapDownloader.getInstance().display(avatar, -1, -1,
						accountBean.getInfo().getAvatar_large(),
						FileLocationMethod.avatar_large);
				nickname.setText(accountBean.getUsernick());
				
				view.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						Intent start=MainTimeLineActivity.newIntent(accountBean);
						getActivity().startActivity(start);
						getActivity().finish();
					}
				});
			}else{
				avatar.setVisibility(View.GONE);
				nickname.setTextColor(getResources().getColor(R.color.gray));
				nickname.setText(getString(R.string.dont_have_other_account));
			}
			
			return view;
		}

	}

	private SlidingMenu getSlidingMenu() {
		return ((MainTimeLineActivity) getActivity()).getSlidingMenu();
	}

	private void setTitle(int res) {
		((MainTimeLineActivity) getActivity()).setTitle(res);
	}

	private void setTitle(String title) {
		((MainTimeLineActivity) getActivity()).setTitle(title);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		final ScrollView view = (ScrollView) inflater.inflate(
				R.layout.slidingdrawer_contents, container, false);
		layout = new Layout();

		layout.avatar = (Spinner) view.findViewById(R.id.avatar);
		layout.nickname = (TextView) view.findViewById(R.id.nickname);

		layout.avatar = (Spinner) view.findViewById(R.id.avatar);
		layout.nickname = (TextView) view.findViewById(R.id.nickname);

		layout.home = (LinearLayout) view.findViewById(R.id.btn_home);
		layout.mention = (LinearLayout) view.findViewById(R.id.btn_mention);
		layout.comment = (LinearLayout) view.findViewById(R.id.btn_comment);
		layout.search = (Button) view.findViewById(R.id.btn_search);
		layout.profile = (Button) view.findViewById(R.id.btn_profile);
		// layout.location = (Button) view.findViewById(R.id.btn_location);
		layout.setting = (Button) view.findViewById(R.id.btn_setting);
		layout.dm = (Button) view.findViewById(R.id.btn_dm);
		layout.logout = (Button) view.findViewById(R.id.btn_logout);
		layout.fav = (Button) view.findViewById(R.id.btn_favourite);
		layout.homeCount = (TextView) view.findViewById(R.id.tv_home_count);
		layout.mentionCount = (TextView) view
				.findViewById(R.id.tv_mention_count);
		layout.commentCount = (TextView) view
				.findViewById(R.id.tv_comment_count);

		boolean blackMagic = GlobalContext.getInstance().getAccountBean()
				.isBlack_magic();
		if (!blackMagic) {
			layout.dm.setVisibility(View.GONE);
			layout.search.setVisibility(View.GONE);
		}

		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		layout.home.setOnClickListener(onClickListener);
		layout.mention.setOnClickListener(onClickListener);
		layout.comment.setOnClickListener(onClickListener);
		layout.search.setOnClickListener(onClickListener);
		layout.profile.setOnClickListener(onClickListener);
		// layout.location.setOnClickListener(onClickListener);
		layout.setting.setOnClickListener(onClickListener);
		layout.dm.setOnClickListener(onClickListener);
		layout.logout.setOnClickListener(onClickListener);
		layout.fav.setOnClickListener(onClickListener);
	}

	private View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_home:
				showHomePage(false);
				drawButtonsBackground(HOME_INDEX);
				break;
			}

		}
	};

	private class Layout {

		Spinner avatar;

		TextView nickname;

		LinearLayout home;

		LinearLayout mention;

		LinearLayout comment;

		TextView homeCount;

		TextView mentionCount;

		TextView commentCount;

		Button search;

		// Button location;
		Button dm;

		Button logout;

		Button profile;

		Button setting;

		Button fav;
	}

}
