package com.stone.support.asyncdrawable;

import com.stone.bean.UserBean;
import com.stone.black.R;
import com.stone.support.debug.AppLogger;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.file.FileManager;
import com.stone.support.imageutility.ImageUtility;
import com.stone.support.lib.MyAsyncTask;
import com.stone.support.settinghelper.SettingUtility;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.ThemeUtility;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.ImageView;

public class TimeLineBitmapDownloader {

	private Handler handler;
	private int defaultPictureResId;

	private static TimeLineBitmapDownloader instance;
	private static final Object lock = new Object();

	static volatile boolean pauseReadWork = false;
	static final Object pauseReadWorkLock = new Object();

	private TimeLineBitmapDownloader(Handler handler) {
		this.handler = handler;
		this.defaultPictureResId = ThemeUtility
				.getResourceId(R.attr.listview_pic_bg);
	}

	public static TimeLineBitmapDownloader getInstance() {
		synchronized (lock) {
			if (instance == null) {
				instance = new TimeLineBitmapDownloader(new Handler(
						Looper.getMainLooper()));
			}
		}

		return instance;
	}

	public static void refreshThemePictureBackground() {
		synchronized (lock) {
			instance = new TimeLineBitmapDownloader(new Handler(
					Looper.getMainLooper()));
		}
	}

	protected Bitmap getBitmapFromMemCache(String key) {
		if (TextUtils.isEmpty(key)) {
			return null;
		} else {
			return GlobalContext.getInstance().getBitmapCache().get(key);
		}
	}

	public void setPauseReadWork(boolean pauseWork) {
		synchronized (pauseReadWorkLock) {
			TimeLineBitmapDownloader.pauseReadWork = pauseWork;
			if (!TimeLineBitmapDownloader.pauseReadWork) {
				pauseReadWorkLock.notifyAll();
			}
		}
	}

	public void downloadAvatar(ImageView view, UserBean user) {
		downloadAvatar(view, user, false);
	}

	public void downloadAvatar(ImageView view, UserBean user, boolean isFling) {
		if (user == null) {
			view.setImageResource(defaultPictureResId);
			return;
		}

		String url;
		FileLocationMethod method;
		if (SettingUtility.getEnableBigPic()) {
			url = user.getAvatar_large();
			method = FileLocationMethod.avatar_large;
		} else {
			url = user.getProfile_image_url();
			method = FileLocationMethod.avatar_small;
		}

		displayImageView(view, url, method, isFling, false);
	}

	/**
	 * when user open weibo detail, the activity will setResult to previous
	 * Activity, timeline will refresh at the time user press back button to
	 * display the latest repost count and comment count. But sometimes, weibo
	 * detail's pictures are very large that bitmap memory cache was cleared
	 * those timeline bitmap to save memory, app have to read bitmap from sd
	 * card again, the app play annoying animation, this method will check
	 * whether we should read again or not.
	 */
	private boolean shouldReloadPicture(ImageView view, String urlKey) {
		if (urlKey.equals(view.getTag())
				&& view.getDrawable() != null
				&& view.getDrawable() instanceof BitmapDrawable
				&& ((BitmapDrawable) view.getDrawable() != null && ((BitmapDrawable) view
						.getDrawable()).getBitmap() != null)) {
			AppLogger.d("shouldReloadPicture=false");
			return false;
		} else {
			view.setTag(null);
			AppLogger.d("shouldReloadPicture=true");
			return true;
		}
	}

	private void displayImageView(final ImageView view, final String urlKey,
			final FileLocationMethod method, boolean isFling,
			boolean isMultiPictures) {
		view.clearAnimation();

		if (!shouldReloadPicture(view, urlKey)) {
			AppLogger.i("no need to reload drawing");
			return;
		} else {
			AppLogger.i("need to relaod drawing,the urlKey=" + urlKey);
		}

		final Bitmap bitmap = getBitmapFromMemCache(urlKey);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
			view.setTag(urlKey);
			if (view.getAlpha() != 1.0f) {
				view.setAlpha(1.0f);
			}
			cancelPotentialDownload(urlKey, view);
		} else {
			if (isFling) {
				view.setImageResource(defaultPictureResId);
				return;
			}

			if (!cancelPotentialDownload(urlKey, view)) {
				return;
			}

			final ReadWorker newTask = new ReadWorker(view, urlKey, method,
					isMultiPictures);
			AppLogger.i("newTask url=" + newTask.getUrl());
			PictureBitmapDrawable downloadedDrawable = new PictureBitmapDrawable(
					newTask);
			view.setImageDrawable(downloadedDrawable);
			AppLogger.i("set image drawable completed");

			// listview fast scroll performance
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (getBitmapDownloaderTask(view) == newTask) {
						newTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
					}
					return;
				}

			}, 400);
		}
	}

	private static boolean cancelPotentialDownload(String url,
			ImageView imageView) {
		IPictureWorker bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
		if (bitmapDownloaderTask != null) {
			String bitmapUrl = bitmapDownloaderTask.getUrl();
			if (bitmapUrl == null || (!bitmapUrl.equals(url))) {
				if (bitmapDownloaderTask instanceof MyAsyncTask) {
					((MyAsyncTask) bitmapDownloaderTask).cancel(true);
				} else {
					return false;
				}
			}
		}

		return true;
	}

	private static IPictureWorker getBitmapDownloaderTask(ImageView imageView) {
		if (imageView != null) {
			Drawable drawable = imageView.getDrawable();
			if (drawable instanceof PictureBitmapDrawable) {
				PictureBitmapDrawable downloaderDrawable = (PictureBitmapDrawable) drawable;
				return downloaderDrawable.getBitmapDownloaderTask();
			}
		}

		return null;
	}

	public void display(final ImageView imageView, final int width,
			final int height, final String url, final FileLocationMethod method) {
		if (TextUtils.isEmpty(url)) {
			return;
		}

		new MyAsyncTask<Void, Bitmap, Bitmap>() {

			@Override
			protected Bitmap doInBackground(Void... params) {
				Bitmap bitmap = null;
				boolean downloaded = TaskCache.waitForPictureDownload(url,
						null, FileManager.getFilePathFromUrl(url, method),
						method);
				if (downloaded) {
					bitmap = ImageUtility.readNormalPic(
							FileManager.getFilePathFromUrl(url, method), width,
							height);
				}

				return bitmap;
			}

			@Override
			protected void onPostExecute(Bitmap bitmap) {
				super.onPostExecute(bitmap);
				if (bitmap != null) {
					imageView.setImageDrawable(new BitmapDrawable(GlobalContext
							.getInstance().getResources(), bitmap));
				}
			}

		}.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);

	}
}
