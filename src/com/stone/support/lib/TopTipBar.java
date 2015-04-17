package com.stone.support.lib;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import com.stone.bean.ItemBean;
import com.stone.bean.ListBean;
import com.stone.black.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Handler;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class TopTipBar extends TextView {

	// always show toptipbar, or hide itself when handling out of data (for
	// example user scroll down listview)
	public enum Type {
		ALWAYS, AUTO
	}

	private TreeSet<Long> ids = null;
	private boolean disapper = false;
	private Runnable lastRunnable;
	private boolean error;
	private OnChangeListener onChangeListener;
	private Type type;

	private static class TopTipBarComparator implements Comparator<Long>,
			Serializable {

		@Override
		public int compare(Long a, Long b) {
			Long aL = a;
			Long bL = b;
			Long resultL = aL - bL;
			int result = 0;
			if (resultL > 0) {
				result = 1;
			} else if (resultL < 0L) {
				result = -1;
			}

			return result;
		}
	}

	public static interface OnChangeListener {
		public void onChange(int count);
	}

	public void setOnChangeListener(OnChangeListener l) {
		this.onChangeListener = l;
		this.onChangeListener.onChange(ids.size());
	}

	public TopTipBar(Context context) {
		this(context, null);
	}

	public TopTipBar(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}

	public TopTipBar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		type = Type.AUTO;
		ids = new TreeSet<Long>(new TopTipBarComparator());
	}

	public void setType(Type type) {
		this.type = type;
		setCount();
	}

	public TreeSet<Long> getValues() {
		TreeSet<Long> copy = new TreeSet<Long>();
		copy.addAll(this.ids);
		return copy;
	}

	public void setValue(Set<Long> values) {
		this.ids.clear();
		this.ids.addAll(values);
		this.disapper = false;
		setCount();
	}

	public void setValue(ListBean<?, ?> listData, boolean disapper) {
		this.disapper = disapper;
		List<? extends ItemBean> values = listData.getItemList();
		for (ItemBean b : values) {
			if (b != null) {
				ids.add(b.getIdLong());
			}
		}

		setCount();
		if (disapper) {
			disappear(3000);
			ids.clear();
		}

		if (this.onChangeListener != null) {
			this.onChangeListener.onChange(ids.size());
		}
	}

	private void disappear(int duration) {
		if (getVisibility() == View.INVISIBLE || getVisibility() == View.GONE) {
			return;
		}

		if (lastRunnable != null) {
			Handler handler = getHandler();
			if (handler != null) {
				handler.removeCallbacks(lastRunnable);
			}
		}

		lastRunnable = new Runnable() {

			@Override
			public void run() {
				animate().alpha(0).setDuration(300)
						.setListener(new AnimatorListenerAdapter() {

							@Override
							public void onAnimationEnd(Animator animation) {
								super.onAnimationEnd(animation);
								setVisibility(View.VISIBLE);
								animate().alpha(1.0f).setListener(null);
								if (ids.size() > 0) {
									setCount();
								}
							}
						});

			}

		};

		Handler handler = getHandler();
		if (handler != null) {
			handler.postDelayed(lastRunnable, duration);
		}
	}

	private void setCount() {
		this.error = false;
		int count = ids.size();

		if (count > 0) {
			setVisibility(View.VISIBLE);
			setText(String.format(
					getContext().getString(R.string.new_messages_count),
					String.valueOf(ids.size())));
			setBackgroundResource(R.color.top_tip_bar_tip);
		} else {
			disappear(0);
		}
	}

	public void hideCount() {
		if (!error) {
			setVisibility(View.INVISIBLE);
		}
	}

	// helperId can be used to keep TopTipBar stay Visible status
	public void handle(long id, long helperId) {
		if (disapper || id == 0L) {
			return;
		}

		SortedSet tmp = ids.headSet(id, true);
		if (tmp.size() > 0) {
			tmp.clear();
			setCount();

			if (this.onChangeListener != null) {
				this.onChangeListener.onChange(ids.size());
			}
		}

		if (type == Type.ALWAYS) {
			return;
		}

		if (helperId == 0L) {
			return;
		}

		if (ids.contains(helperId)) {
			setCount();
		} else {
			setVisibility(View.INVISIBLE);
		}
	}

	public void clearAndReset() {
		if (disapper) {
			return;
		}
		ids.clear();
		disappear(0);
		if (this.onChangeListener != null) {
			this.onChangeListener.onChange(ids.size());
		}
	}
	
	public void setError(String error){
		this.disapper=true;
		this.error=true;
		setVisibility(View.VISIBLE);
		animate().alpha(1.0f);
		setText(error);
		disappear(3000);
		setBackgroundResource(R.color.top_tip_bar_error);
	}
	
	@Override
	public Parcelable onSaveInstanceState(){
		//begin boilerplate code that allows parent classes to save state
		Parcelable superState=super.onSaveInstanceState();
		SavedState ss=new SavedState(superState);
	}
	
	static class SavedState extends BaseSavedState{
		
		TreeSet<Long> ids;
		boolean disapper;
		boolean visible;

		public SavedState(Parcelable superState) {
			super(superState);
		}
		
		private SavedState
		
	}

}
