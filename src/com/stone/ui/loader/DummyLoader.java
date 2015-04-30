package com.stone.ui.loader;

import com.stone.bean.android.AsyncTaskLoaderResult;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class DummyLoader<T> extends AsyncTaskLoader<AsyncTaskLoaderResult<T>> {

	public DummyLoader(Context context) {
		super(context);
	}
	
	@Override
	protected void onStartLoading(){
		super.onStartLoading();
		forceLoad();
	}

	@Override
	public AsyncTaskLoaderResult<T> loadInBackground() {
		return null;
	}

}
