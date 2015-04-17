package com.stone.support.asyncdrawable;

import java.lang.ref.WeakReference;

import com.stone.black.R;
import com.stone.support.utils.ThemeUtility;

import android.graphics.drawable.ColorDrawable;

public class PictureBitmapDrawable extends ColorDrawable {
	private final WeakReference<IPictureWorker> bitmapDownloaderTaskReference;

	public PictureBitmapDrawable(IPictureWorker bitmapDownloaderTask) {
		super(ThemeUtility.getColor(R.attr.listview_pic_bg));
		bitmapDownloaderTaskReference = new WeakReference<IPictureWorker>(
				bitmapDownloaderTask);
	}

	public IPictureWorker getBitmapDownloaderTask() {
		return bitmapDownloaderTaskReference.get();
	}
}
