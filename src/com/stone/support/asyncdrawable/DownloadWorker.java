package com.stone.support.asyncdrawable;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

import com.stone.support.file.FileDownloaderHttpHelper;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.file.FileManager;
import com.stone.support.imageutility.ImageUtility;
import com.stone.support.lib.MyAsyncTask;

public class DownloadWorker extends MyAsyncTask<String, Integer, Boolean>
		implements IPictureWorker {

	private String url = "";
	private CopyOnWriteArrayList<FileDownloaderHttpHelper.DownloadListener> downloadListenerList = new CopyOnWriteArrayList<FileDownloaderHttpHelper.DownloadListener>();

	private FileLocationMethod method;

	@Override
	public String getUrl() {
		return url;
	}

	public DownloadWorker(String url, FileLocationMethod method) {
		this.url = url;
		this.method = method;
	}

	public void addDownloadListener(
			FileDownloaderHttpHelper.DownloadListener listener) {
		downloadListenerList.addIfAbsent(listener);
	}

	@Override
	protected Boolean doInBackground(String... params) {
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
			return false;
		}

		String filePath = FileManager.getFilePathFromUrl(url, method);
		String actualDownloadUrl = url;

		switch (method) {
		case picture_thumbnail:
			actualDownloadUrl = url.replace("thumbnail", "webp180");
			break;
		case picture_bmiddle:
			actualDownloadUrl = url.replace("bmiddle", "webp720");
			break;
		case picture_large:
			actualDownloadUrl = url.replace("large", "woriginal");
			break;

		}

		boolean result = ImageUtility.getBitmapFromNetWork(actualDownloadUrl,
				filePath, new FileDownloaderHttpHelper.DownloadListener() {
					@Override
					public void pushProgress(int progress, int max) {
						publishProgress(progress, max);
					}
				});

		TaskCache.removeDownloadTask(url, DownloadWorker.this);
		return result;
	}

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);

		for (FileDownloaderHttpHelper.DownloadListener downloadListener : downloadListenerList) {
			if (downloadListener != null) {
				downloadListener.pushProgress(values[0], values[1]);
			}
		}
	}


}
