package com.stone.othercomponent;

import java.util.ArrayList;
import java.util.HashMap;

import android.text.TextUtils;

import com.stone.bean.MessageBean;
import com.stone.bean.MessageListBean;
import com.stone.bean.UserBean;
import com.stone.support.asyncdrawable.TaskCache;
import com.stone.support.debug.AppLogger;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.file.FileManager;
import com.stone.support.lib.VelocityListView;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;

public class WifiAutoDownloadPictureRunnable implements Runnable {

	private MessageListBean msgList;
	private static HashMap<String, Boolean> result = new HashMap<String, Boolean>();

	public WifiAutoDownloadPictureRunnable(MessageListBean source,
			int position, int listViewScrollOrientation) {
		this.msgList = source;
		switch (listViewScrollOrientation) {
		case VelocityListView.TOWARDS_BOTTOM:
			for (int i = position; i < source.getSize(); i++) {
				MessageBean msg = source.getItem(i);
				if (msg != null) {
					this.msgList.getItemList().add(msg);
				}
			}
			break;
		case VelocityListView.TOWARDS_TOP:
			for (int i = position; i >= 0; i--) {
				MessageBean msg = source.getItem(i);
				if (msg != null) {
					this.msgList.getItemList().add(msg);
				}
			}
			break;
		}

		AppLogger.i("WifiAutoDownloadPictureRunnable new Runnable");
	}

	@Override
	public void run() {
		for (MessageBean msg : this.msgList.getItemList()) {
            if (!continueHandleMessage(msg)) {
                return;
            }
        }

	}
	
	private boolean continueHandleMessage(MessageBean msg) {

        if (Thread.currentThread().isInterrupted()) {
            return false;
        }

        if (!Utility.isWifi(GlobalContext.getInstance())) {
            return false;
        }

        if (msg == null) {
            return true;
        }

        Boolean done = result.get(msg.getId());
        if (done != null && done) {
            AppLogger.i("already done skipped");
            return true;
        }

        //wait until other download tasks are finished
        synchronized (TaskCache.backgroundWifiDownloadPicturesWorkLock) {
            while (!TaskCache.isDownloadTaskFinished() && !Thread.currentThread()
                    .isInterrupted()) {
                try {
                    AppLogger.i("WifiAutoDownloadPictureRunnable wait for lock");
                    TaskCache.backgroundWifiDownloadPicturesWorkLock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }

        AppLogger.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "start");
        startDownload(msg);
        AppLogger.i("WifiAutoDownloadPictureRunnable" + msg.getId() + "finished");

        result.put(msg.getId(), true);
        return true;
    }

	private void startDownload(MessageBean msg) {

		if (!msg.isMultiPics()) {
			String url = msg.getOriginal_pic();
			if (!TextUtils.isEmpty(url)) {
				downloadPic(url);
			}
		} else {
			ArrayList<String> urls = msg.getHighPicUrls();
			for (String url : urls) {
				downloadPic(url);
			}
		}

		MessageBean reTweetedMsg = msg.getRetweeted_status();

		if (reTweetedMsg != null) {
			if (!reTweetedMsg.isMultiPics()) {
				String url = reTweetedMsg.getOriginal_pic();
				if (!TextUtils.isEmpty(url)) {
					downloadPic(url);
				}
			} else {
				ArrayList<String> urls = reTweetedMsg.getHighPicUrls();
				for (String url : urls) {
					downloadPic(url);
				}
			}
		}

		UserBean user = msg.getUser();
		if (user != null) {
			downloadAvatar(user.getAvatar_large());
		}
	}

	private void downloadPic(String url) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		String path = FileManager.getFilePathFromUrl(url,
				FileLocationMethod.picture_large);
		TaskCache.waitForPictureDownload(url, null, path,
				FileLocationMethod.picture_large);
	}

	private void downloadAvatar(String url) {
		if (TextUtils.isEmpty(url)) {
			return;
		}
		String path = FileManager.getFilePathFromUrl(url,
				FileLocationMethod.avatar_large);
		TaskCache.waitForPictureDownload(url, null, path,
				FileLocationMethod.avatar_large);
	}

}
