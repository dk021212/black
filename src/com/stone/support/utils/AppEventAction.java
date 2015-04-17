package com.stone.support.utils;

import com.stone.bean.MessageBean;

import android.content.IntentFilter;

public class AppEventAction {
	// use ordered broadcast to decide to use which method to show new message
	// notification,
	// Android notification bar or black activity if user has opened this app,
	// activity can interrupt the broadcast
	// Must equal AndroidManifest's
	// .othercomponent.unreadontification.UnreadMsgReceiver action name
	public static final String NEW_MSG_PRIORITY_BROADCAST = "com.stone.black.newmsg.priority";

	// metions weibo, metions comment, comment to me fragment use this broadcast
	// to receive actual data
	public static final String NEW_MSG_BROADCAST = "com.stone.black.newmsg";

	public static IntentFilter getSystemMusicBroadcastFilterAction() {
		IntentFilter musicFilter = new IntentFilter();
		musicFilter.addAction("com.android.music.metachanged");
		musicFilter.addAction("com.android.music.playstatechanged");
		musicFilter.addAction("com.android.music.playbackcomplete");
		musicFilter.addAction("com.android.music.queuechanged");

		musicFilter.addAction("com.htc.music.metachanged");
		musicFilter.addAction("fm.last.android.metachanged");
		musicFilter.addAction("com.sec.android.app.music.metachanged");
		musicFilter.addAction("com.nullsoft.winamp.metachanged");
		musicFilter.addAction("com.amazon.mp3.metachanged");
		musicFilter.addAction("com.miui.player.metachanged");
		musicFilter.addAction("com.real.IMP.metachanged");
		musicFilter.addAction("com.sonyericsson.music.metachanged");
		musicFilter.addAction("com.rdio.android.metachanged");
		musicFilter
				.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
		musicFilter.addAction("com.andrew.apollo.metachanged");
		return musicFilter;
	}

	public static final String SLIDING_MENU_CLOSED_BROADCASST = "com.stone.black.slidingmenu_closed";

	private static final String SEND_COMMENT_OR_REPLY_SUCCESSFULLY = "com.stone.black.SEND.COMMENT.COMPLETED";

	private static final String SEND_REPOST_SUCCESSFULLY = "com.stone.black.SEND_REPOST.COMPLETED";

	public static String buildSendCommentOrReplySuccessfullyAction(
			MessageBean oriMsg) {
		return SEND_COMMENT_OR_REPLY_SUCCESSFULLY + oriMsg.getId();
	}

	public static String buildSendRepostSuccessfullyAction(MessageBean oriMsg) {
		return SEND_REPOST_SUCCESSFULLY + oriMsg.getId();
	}

}
