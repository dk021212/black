package com.stone.support.asyncdrawable;

import java.lang.ref.WeakReference;

import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.stone.black.R;
import com.stone.support.debug.AppLogger;
import com.stone.support.file.FileDownloaderHttpHelper;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.file.FileManager;
import com.stone.support.imageutility.ImageUtility;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;

public class ReadWorker extends MyAsyncTask<String, Integer, Bitmap> implements
		IPictureWorker {

	private LruCache<String, Bitmap> lruCache;
	private String data = "";
	private WeakReference<ImageView> viewWeakReference;

	private GlobalContext globalContext;
	private FileLocationMethod method;
	private FailedResult failedResult;
	private int mShortAnimationDuration;
	private WeakReference<ProgressBar> pbWeakReference;
	private boolean isMultiPictures = false;
	private IBlackDrawable IBlackDrawable;

	@Override
	public String getUrl() {
		return data;
	}

	public ReadWorker(ImageView view, String url, FileLocationMethod method,
			boolean isMultiPictures) {
		this.globalContext = GlobalContext.getInstance();
		this.lruCache = globalContext.getBitmapCache();
		this.viewWeakReference = new WeakReference<ImageView>(view);
		this.data = url;
		this.method = method;
		this.mShortAnimationDuration = GlobalContext.getInstance()
				.getResources()
				.getInteger(android.R.integer.config_shortAnimTime);
		this.isMultiPictures = isMultiPictures;
	}

	public ReadWorker(ImageView view, String url, FileLocationMethod method) {
		this(view, url, method, false);
	}

	public ReadWorker(IBlackDrawable view, String url,
			FileLocationMethod method, boolean isMultiPictures) {
		this(view.getImageView(), url, method);
		this.IBlackDrawable = view;
		this.pbWeakReference = new WeakReference<ProgressBar>(
				view.getProgressBar());
		view.setGifFlag(false);
		if (SettingUtility.getEnableBigPic()) {
			if (view.getProgressBar() != null) {
				view.getProgressBar().setVisibility(View.VISIBLE);
				view.getProgressBar().setProgress(0);
			}
		} else {
			if (view.getProgressBar() != null) {
				view.getProgressBar().setVisibility(View.INVISIBLE);
				view.getProgressBar().setProgress(0);
			}
		}
		this.isMultiPictures = isMultiPictures;
	}

	public ReadWorker(IBlackDrawable view, String url, FileLocationMethod method) {
		this(view, url, method, false);
	}

	@Override
	protected Bitmap doInBackground(String... params) {
		AppLogger.d("Read Worker doInBackground");
		synchronized (TimeLineBitmapDownloader.pauseReadWorkLock) {
			while (TimeLineBitmapDownloader.pauseReadWork && !isCancelled()) {
				try {
					TimeLineBitmapDownloader.pauseReadWorkLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		if (isCancelled()) {
			return null;
		}

		String path = FileManager.getFilePathFromUrl(data, method);
		AppLogger.i("path=" + path);
		boolean download = TaskCache.waitForPictureDownload(data,
				(SettingUtility.getEnableBigPic() ? downloadListener : null),
				path, method);

		if (!download) {
			failedResult = FailedResult.downloadFailed;
			return null;
		}

		int height = 0;
		int width = 0;

		switch (method) {
		case avatar_small:
		case avatar_large:
			width = globalContext.getResources().getDimensionPixelSize(
					R.dimen.timeline_avatar_width)
					- Utility.dip2px(5) * 2;
			height = globalContext.getResources().getDimensionPixelSize(
					R.dimen.timeline_avatar_height)
					- Utility.dip2px(5) * 2;
			break;
		case picture_thumbnail:
			width = globalContext.getResources().getDimensionPixelSize(
					R.dimen.timeline_pic_thumbnail_width);
			height = globalContext.getResources().getDimensionPixelSize(
					R.dimen.timeline_pic_thumbnail_height);
			break;
		case picture_large:
		case picture_bmiddle:
			if (!isMultiPictures) {
				DisplayMetrics metrics = globalContext.getDisplayMetrics();
				float reSize = globalContext.getResources().getDisplayMetrics().density;
				height = globalContext.getResources().getDimensionPixelSize(
						R.dimen.timeline_pic_high_thumbnail_height);
				// 8 is layout padding
				width = (int) (metrics.widthPixels - (8 + 8) * reSize);
			} else {
				height = width = Utility.dip2px(120);
			}
			break;
		}

		synchronized (TimeLineBitmapDownloader.pauseReadWorkLock) {
			while (TimeLineBitmapDownloader.pauseReadWork && isCancelled()) {
				try {
					TimeLineBitmapDownloader.pauseReadWorkLock.wait();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}

		if (isCancelled()) {
			return null;
		}

		Bitmap bitmap;
		switch (method) {
		case avatar_small:
		case avatar_large:
			bitmap = ImageUtility.getRoundedCornerPic(path, width, height,
					Utility.dip2px(2));
			break;
		default:
			bitmap = ImageUtility.getRoundedCornerPic(path, width, height, 0);
			break;
		}

		if (bitmap == null) {
			this.failedResult = FailedResult.readFailed;
			AppLogger.i("bitmap is null");
		}

		return bitmap;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		if (TimeLineBitmapDownloader.pauseReadWork) {
			return;
		}

		ImageView imageView = viewWeakReference.get();
		if (imageView != null) {
			if (canDisplay(imageView) && pbWeakReference != null) {
				ProgressBar pb = pbWeakReference.get();
				if (pb != null) {
					Integer progress = values[0];
					Integer max = values[1];
					pb.setMax(max);
					pb.setProgress(progress);
				}
			} else if (isImageViewDrawableBitmap(imageView)) {
				// imageview drawable actually is bitmap, so hide progressbar
				resetProgressBarStatues();
				pbWeakReference = null;
			}
		}
	}

	@Override
	protected void onCancelled(Bitmap bitmap) {
		super.onCancelled(bitmap);
		this.failedResult = FailedResult.taskCanceled;
		displayBitmap(bitmap);
	}

	@Override
	protected void onPostExecute(Bitmap bitmap) {
		super.onPostExecute(bitmap);
		displayBitmap(bitmap);
	}

	private void displayBitmap(Bitmap bitmap) {
		ImageView imageView = viewWeakReference.get();
		if (imageView != null) {
			if (canDisplay(imageView)) {
				if (pbWeakReference != null) {
					ProgressBar pb = pbWeakReference.get();
					if (pb != null) {
						pb.setVisibility(View.INVISIBLE);
					}
				}

				if (bitmap != null) {
					if (IBlackDrawable != null)
						IBlackDrawable.setGifFlag(ImageUtility
								.isThisPictureGif(getUrl()));
					playImageViewAnimation(imageView, bitmap);
					lruCache.put(data, bitmap);
				} else if (failedResult != null) {
					switch (failedResult) {
					case downloadFailed:
						imageView.setImageDrawable(new ColorDrawable(
								DebugColor.DOWNLOAD_FAILED));
						break;
					case readFailed:
						imageView.setImageDrawable(new ColorDrawable(
								DebugColor.PICTURE_ERROR));
						break;
					case taskCanceled:
						imageView.setImageDrawable(new ColorDrawable(
								DebugColor.DOWNLOAD_CANCEL));
						break;
					}

				}

			} else if (isImageViewDrawableBitmap(imageView)) {
				// imageview drawable actually is bitmap, so hide progressbar
				resetProgressBarStatues();
			}
		}
	}

	private boolean canDisplay(ImageView view) {
		if (view != null) {
			IPictureWorker bitmapDownloaderTask = getBitmapDownloaderTask(view);
			if (this == bitmapDownloaderTask) {
				return true;
			}
		}
		return false;
	}

	private static IPictureWorker getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof PictureBitmapDrawable) {
				PictureBitmapDrawable downloadedDrawable = (PictureBitmapDrawable) drawable;
				return downloadedDrawable.getBitmapDownloaderTask();
			}
		}

		return null;
	}

	private boolean isImageViewDrawableBitmap(ImageView imageView) {
		return !(imageView.getDrawable() instanceof PictureBitmapDrawable);
	}

	private void resetProgressBarStatues() {
		if (pbWeakReference == null) {
			return;
		}
		ProgressBar pb = pbWeakReference.get();
		if (pb != null) {
			pb.setVisibility(View.INVISIBLE);
		}
	}

	private void playImageViewAnimation(final ImageView view,
			final Bitmap bitmap) {
		view.setImageBitmap(bitmap);
		resetProgressBarStatues();
		AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1.0f);
		alphaAnimation.setDuration(500);
		view.startAnimation(alphaAnimation);
		view.setTag(getUrl());
	}

	FileDownloaderHttpHelper.DownloadListener downloadListener = new FileDownloaderHttpHelper.DownloadListener() {

		@Override
		public void pushProgress(int progress, int max) {
			onProgressUpdate(progress, max);
		}

		@Override
		public void completed() {

		}

		@Override
		public void cancel() {

		}
	};

}
