package com.stone.support.asyncdrawable;

import com.stone.bean.MessageBean;
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
import com.stone.ui.basefragment.AbstractTimeLineFragment;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

public class TimeLineBitmapDownloader {

	private Handler handler;
	private int defaultPictureResId;

	private static TimeLineBitmapDownloader instance;
	private static final Object lock = new Object();

	static volatile boolean pauseDownloadWork = false;
	static final Object pauseDownloadWorkLock = new Object();

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

	public void downloadAvatar(ImageView view, UserBean user,
			AbstractTimeLineFragment fragment) {
		boolean isFling = fragment.isListViewFling();
		downloadAvatar(view, user, isFling);
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

	public void displayMultiPicture(IBlackDrawable view, String picUrl,
			FileLocationMethod method, AbstractTimeLineFragment fragment) {

		boolean isFling = ((AbstractTimeLineFragment) fragment)
				.isListViewFling();

		display(view, picUrl, method, isFling, true);

	}

	public void displayMultiPicture(IBlackDrawable view, String picUrl,
			FileLocationMethod method) {

		display(view, picUrl, method, false, true);

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

	private void display(final IBlackDrawable view, final String urlKey,
			final FileLocationMethod method, boolean isFling,
			boolean isMultiPictures) {
		view.getImageView().clearAnimation();

		if (!shouldReloadPicture(view.getImageView(), urlKey)) {
			return;
		}

		final Bitmap bitmap = getBitmapFromMemCache(urlKey);
		if (bitmap != null) {
			view.setImageBitmap(bitmap);
			view.getImageView().setTag(urlKey);
			if (view.getProgressBar() != null) {
				view.getProgressBar().setVisibility(View.INVISIBLE);
			}
			if (view.getImageView().getAlpha() != 1.0f) {
				view.getImageView().setAlpha(1.0f);
			}

			view.setGifFlag(ImageUtility.isThisPictureGif(urlKey));
			cancelPotentialDownload(urlKey, view.getImageView());
		} else {
			if (isFling) {
				view.getImageView().setImageResource(defaultPictureResId);
				if (view.getProgressBar() != null) {
					view.getProgressBar().setVisibility(View.INVISIBLE);
				}
				view.setGifFlag(ImageUtility.isThisPictureGif(urlKey));
				return;
			}

			if (!cancelPotentialDownload(urlKey, view.getImageView())) {
				return;
			}

			final ReadWorker newTask = new ReadWorker(view, urlKey, method,
					isMultiPictures);
			PictureBitmapDrawable downloaderDrawable = new PictureBitmapDrawable(
					newTask);
			view.setImageDrawable(downloaderDrawable);

			// listview fast scroll peformance
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					if (getBitmapDownloaderTask(view.getImageView()) == newTask) {
						newTask.executeOnExecutor(MyAsyncTask.THREAD_POOL_EXECUTOR);
					}
					return;
				}

			}, 400);
		}
	}

	public void downContentPic(ImageView view, MessageBean msg,
			AbstractTimeLineFragment fragment) {
		String picUrl;
		boolean isFling = ((AbstractTimeLineFragment) fragment)
				.isListViewFling();

		if (SettingUtility.getEnableBigPic()) {
			picUrl = msg.getOriginal_pic();
			displayImageView(view, picUrl, FileLocationMethod.picture_large,
					isFling, false);
		} else {
			picUrl = msg.getThumbnail_pic();
			displayImageView(view, picUrl,
					FileLocationMethod.picture_thumbnail, isFling, false);
		}

	}

	public void downContentPic(IBlackDrawable view, MessageBean msg,
			AbstractTimeLineFragment fragment) {
		String picUrl;

		boolean isFling = ((AbstractTimeLineFragment) fragment)
				.isListViewFling();

		if (SettingUtility.getEnableBigPic()) {
			picUrl = msg.getOriginal_pic();
			display(view, picUrl, FileLocationMethod.picture_large, isFling,
					false);

		} else {
			picUrl = msg.getThumbnail_pic();
			display(view, picUrl, FileLocationMethod.picture_thumbnail,
					isFling, false);

		}
	}

	/**
	 * Pause any ongoing background work. This can be used as a temporary
	 * measure to improve performance. For example background work could be
	 * paused when a ListView or GridView is being scrolled using a
	 * {@link android.widget.AbsListView.OnScrollListener} to keep scrolling
	 * smooth.
	 * <p/>
	 * If work is paused, be sure setPauseDownloadWork(false) is called again
	 * before your fragment or activity is destroyed (for example during
	 * {@link android.app.Activity#onPause()}), or there is a risk the
	 * background thread will never finish.
	 */
	public void setPauseDownloadWork(boolean pauseWork) {
		synchronized (pauseDownloadWorkLock) {
			TimeLineBitmapDownloader.pauseDownloadWork = pauseWork;
			if (!TimeLineBitmapDownloader.pauseDownloadWork) {
				pauseDownloadWorkLock.notifyAll();
			}
		}
	}
}
