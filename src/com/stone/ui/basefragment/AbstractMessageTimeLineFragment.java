package com.stone.ui.basefragment;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Toast;

import com.stone.bean.ListBean;
import com.stone.bean.MessageBean;
import com.stone.black.R;
import com.stone.dao.destroy.DestroyStatusDao;
import com.stone.support.error.WeiboException;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;
import com.stone.ui.actionmenu.StatusSingleChoiceModeListener;
import com.stone.ui.adapter.StatusListAdapter;
import com.stone.ui.interfaces.IRemoveItem;

public abstract class AbstractMessageTimeLineFragment<T extends ListBean<MessageBean, ?>>
		extends AbstractTimeLineFragment<T> implements IRemoveItem {

	private RemoveTask removeTask;

	protected void showNewMsgToastMessage(ListBean<MessageBean, ?> newValue) {
		if (newValue != null && getActivity() != null) {
			if (newValue.getSize() == 0) {
				Toast.makeText(getActivity(),
						getString(R.string.no_new_message), Toast.LENGTH_SHORT)
						.show();
			} else if (newValue.getSize() > 0) {
				Toast.makeText(
						getActivity(),
						getString(R.string.total) + newValue.getSize()
								+ getString(R.string.new_messages),
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	protected void clearAndReplaceValue(ListBean<MessageBean, ?> value) {
		getList().getItemList().clear();
		getList().getItemList().addAll(value.getItemList());
		getList().setTotal_number(value.getTotal_number());
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		getListView().setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
		getListView().setOnItemLongClickListener(onItemLongClickListener);
	}

	private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {

		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {

			if (position - getListView().getHeaderViewsCount() < getList()
					.getSize()
					&& position - getListView().getHeaderViewsCount() >=0
					&& timeLineAdapter.getItem(position
							- getListView().getHeaderViewsCount()) != null) {
				MessageBean msg = getList().getItemList().get(
						position - getListView().getHeaderViewsCount());
				StatusSingleChoiceModeListener choiceModeListener = new StatusSingleChoiceModeListener(
						getListView(), (StatusListAdapter) timeLineAdapter,
						AbstractMessageTimeLineFragment.this, msg);

				if (mActionMode != null) {
					mActionMode.finish();
					mActionMode = null;
				}

				getListView().setItemChecked(position, true);
				getAdapter().notifyDataSetChanged();
				mActionMode = getActivity().startActionMode(choiceModeListener);
				return true;
			}
			return false;
		}

	};

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	/**
	 * fix android bug,long press a item in the first tab's listview, rotate
	 * screen, the item background is still blue(it is checked), but if you test
	 * on other tabs' lstview, the item is not checked
	 */
	@Override
	public void onResume() {
		super.onResume();
		clearActionMode();
	}

	@Override
	protected void buildListAdapter() {
		timeLineAdapter = new StatusListAdapter(this, getList().getItemList(),
				getListView(), true);
		getListView().setAdapter(timeLineAdapter);
	}

	@Override
	public void removeItem(int position) {
		clearActionMode();
		if (removeTask == null
				|| removeTask.getStatus() == MyAsyncTask.Status.FINISHED) {
			removeTask = new RemoveTask(GlobalContext.getInstance()
					.getSpecialToken(), getList().getItemList().get(position)
					.getId(), position);
			removeTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
		}
	}

	@Override
	public void removeCancel() {
		clearActionMode();
	}

	class RemoveTask extends MyAsyncTask<Void, Void, Boolean> {

		String token;
		String id;
		int position;
		WeiboException e;

		public RemoveTask(String token, String id, int position) {
			this.token = token;
			this.id = id;
			this.position = position;
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
			if (Utility.isAllNotNull(this.e, getActivity())) {
				Toast.makeText(getActivity(), e.getError(), Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			super.onPostExecute(aBoolean);
			if (aBoolean) {
				((StatusListAdapter) timeLineAdapter).removeItem(position);
			}
		}

	}

}
