package com.stone.support.settinghelper;

import android.content.Context;

import com.stone.black.R;
import com.stone.support.utils.AppConfig;
import com.stone.support.utils.GlobalContext;
import com.stone.support.utils.Utility;
import com.stone.ui.preference.SettingActivity;

public class SettingUtility {

	private static final String FIRSTSTART = "firststart";
	private static final String LAST_FOUND_WEIBO_ACCOUNT_LINK = "last_found_weibo_account_link";

	private static Context getContext() {
		return GlobalContext.getInstance();
	}

	public static int getAppTheme() {
		String value = SettingHelper.getSharedPreferences(getContext(),
				SettingActivity.THEME, "1");
		switch (Integer.valueOf(value)) {
		case 1:
			return R.style.AppTheme_Light;
		case 2:
			return R.style.AppTheme_Dark;
		default:
			return R.style.AppTheme_Light;
		}
	}

	public static String getDefaultAccountId() {
		return SettingHelper.getSharedPreferences(getContext(), "id", "");
	}

	public static boolean getEnableBigPic() {
		return SettingHelper.getSharedPreferences(getContext(),
				SettingActivity.SHOW_BIG_PIC, false);
	}

	public static boolean isEnablePic() {
		return !SettingHelper.getSharedPreferences(getContext(),
				SettingActivity.DISABLE_DOWNLOAD_AVATAR_PIC, false);
	}

	public static boolean firstStart() {
		boolean value = SettingHelper.getSharedPreferences(getContext(),
				FIRSTSTART, true);
		if (value) {
			SettingHelper.setEditor(getContext(), FIRSTSTART, false);
		}

		return value;
	}

	public static boolean allowInternalWebBrowser() {
		return SettingHelper.getSharedPreferences(getContext(),
				SettingActivity.ENABLE_INTERNAL_WEB_BROWSER, true);

	}

	public static String getMsgCount() {
		String value = SettingHelper.getSharedPreferences(getContext(),
				SettingActivity.MSG_COUNT, "3");
		switch (Integer.valueOf(value)) {
		case 1:
			return String.valueOf(AppConfig.DEFAULT_MSG_COUNT_25);
		case 2:
			return String.valueOf(AppConfig.DEFAULT_MSG_COUNT_50);
		case 3:
			if (Utility.isConnected(getContext())) {
				if (Utility.isWifi(getContext())) {
					return String.valueOf(AppConfig.DEFAULT_MSG_COUNT_50);
				} else {
					return String.valueOf(AppConfig.DEFAULT_MSG_COUNT_25);
				}
			}
		}

		return String.valueOf(AppConfig.DEFAULT_MSG_COUNT_25);
	}

	public static void setDefaultAccountId(String id) {
		SettingHelper.setEditor(getContext(), "id", id);
	}

	public static String getLastFoundWeiboAccountLink() {
        return SettingHelper.getSharedPreferences(getContext(), LAST_FOUND_WEIBO_ACCOUNT_LINK, "");
    }

    public static void setLastFoundWeiboAccountLink(String url) {
        SettingHelper.setEditor(getContext(), LAST_FOUND_WEIBO_ACCOUNT_LINK, url);
    }
    
    public static boolean disableFetchAtNight() {
        return SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.DISABLE_FETCH_AT_NIGHT, true)
                && Utility.isSystemRinger(getContext());
    }

    public static boolean allowCommentToMe() {
        return SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.ENABLE_COMMENT_TO_ME, true);

    }

    public static boolean allowMentionToMe() {
        return SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.ENABLE_MENTION_TO_ME, true);

    }

    public static boolean allowMentionCommentToMe() {
        return SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.ENABLE_MENTION_COMMENT_TO_ME,
                        true);

    }

    public static String getFrequency() {
        return SettingHelper.getSharedPreferences(getContext(), SettingActivity.FREQUENCY, "1");
    }

    public static boolean getEnableFetchMSG() {
        return SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.ENABLE_FETCH_MSG, false);
    }

    public static int getListAvatarMode() {
        String value = SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.LIST_AVATAR_MODE, "1");
        return Integer.valueOf(value);
    }

	public static void setEnableBigAvatar(boolean value) {
        SettingHelper.setEditor(getContext(), SettingActivity.SHOW_BIG_AVATAR, value);
    }

	public static int getListPicMode() {
        String value = SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.LIST_PIC_MODE, "1");
        return Integer.valueOf(value);
    }

	public static void setEnableBigPic(boolean value) {
        SettingHelper.setEditor(getContext(), SettingActivity.SHOW_BIG_PIC, value);
    }

	public static int getCommentRepostAvatar() {
        String value = SettingHelper
                .getSharedPreferences(getContext(), SettingActivity.COMMENT_REPOST_AVATAR, "1");
        return Integer.valueOf(value);
    }

	public static void setEnableCommentRepostAvatar(boolean value) {
        SettingHelper.setEditor(getContext(), SettingActivity.SHOW_COMMENT_REPOST_AVATAR, value);
    }
}
