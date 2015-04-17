package com.stone.support.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.stone.black.R;
import com.stone.support.debug.AppLogger;
import com.stone.support.utils.GlobalContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.Toast;

public class FileManager {

	private static final String AVATAR_SMALL = "avater_small";
	private static final String AVATAR_LARGE = "avater_large";
	private static final String PICTURE_THUMBNAIL = "picture_thumbnail";
	private static final String PICTURE_BMIDDLE = "picture_bmiddle";
	private static final String PICTURE_LARGE = "picture_large";
	private static final String MAP = "map";
	private static final String COVER = "cover";
	private static final String EMOTION = "emotion";
	private static final String TXT2PIC = "txt2pic";
	private static final String WEBVIEW_FAVICON = "favicon";

	private static final String LOG = "log";

	/**
	 * instan black, open black and login in, Android system will create cache
	 * dir. then open cache dir(/sdcard dir/Android/data/com.stone.black) with
	 * Root Explorer, uninstall black and reinstall it, the new app will have
	 * the bug it can't read cache dir again, so I have to tell user to delete
	 * that cache dir
	 */
	private static volatile boolean cantReadBecauseOfAndroidBugPermissionProblem = false;

	private static String getSdCardPath() {
		if (isExternalStorageMounted()) {
			File path = GlobalContext.getInstance().getExternalCacheDir();
			if (path != null) {
				return path.getAbsolutePath();
			} else {
				if (!cantReadBecauseOfAndroidBugPermissionProblem) {
					cantReadBecauseOfAndroidBugPermissionProblem = true;
					final Activity activity = GlobalContext.getInstance()
							.getActivity();
					if (activity == null || activity.isFinishing()) {
						GlobalContext.getInstance().getUIHandler()
								.post(new Runnable() {

									@Override
									public void run() {
										Toast.makeText(
												GlobalContext.getInstance(),
												R.string.please_delete_cache_dir,
												Toast.LENGTH_LONG).show();

									}

								});
						return "";
					}// if end
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							new AlertDialog.Builder(activity)
									.setTitle(R.string.something_error)
									.setMessage(
											R.string.please_delete_cache_dir)
									.setPositiveButton(
											R.string.ok,
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													// TODO Auto-generated
													// method stub

												}
											}).show();

						}

					});

				}
			}
		} else {
			return "";
		}

		return "";
	}

	public static boolean isExternalStorageMounted() {
		boolean canRead = Environment.getExternalStorageDirectory().canRead();
		boolean onlyRead = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED_READ_ONLY);
		boolean unMounted = Environment.getExternalStorageState().equals(
				Environment.MEDIA_UNMOUNTED);

		return !(!canRead || onlyRead || unMounted);
	}

	public static String getLogDir() {
		if (!isExternalStorageMounted()) {
			return "";
		} else {
			String path = getSdCardPath() + File.separator + LOG;

			if (!new File(path).exists()) {
				new File(path).mkdirs();
			}

			return path;
		}
	}

	public static String getFilePathFromUrl(String url,
			FileLocationMethod method) {
		if (!isExternalStorageMounted()) {
			return "";
		}

		if (TextUtils.isEmpty(url)) {
			return "";
		}

		int index = url.indexOf("//");

		String s = url.substring(index + 2);
		String oldRelativePath = s.substring(s.indexOf("/"));

		String newRelativePath = "";

		switch (method) {
		case avatar_small:
			newRelativePath = AVATAR_SMALL + oldRelativePath;
			break;
		case avatar_large:
			newRelativePath = AVATAR_LARGE + oldRelativePath;
			break;
		case picture_thumbnail:
			newRelativePath = PICTURE_THUMBNAIL + oldRelativePath;
			break;
		case picture_bmiddle:
			newRelativePath = PICTURE_BMIDDLE + oldRelativePath;
			break;
		case picture_large:
			newRelativePath = PICTURE_LARGE + oldRelativePath;
			break;
		case emotion:
			String name = new File(oldRelativePath).getName();
			newRelativePath = EMOTION + File.separator + name;
			break;
		case cover:
			newRelativePath = COVER + oldRelativePath;
			break;
		case map:
			newRelativePath = MAP + oldRelativePath;
			break;
		}

		String result = getSdCardPath() + File.separator + newRelativePath;
		if (!result.endsWith(".jpg") && !result.endsWith(".gif")
				&& !result.endsWith(".png")) {
			result = result + ".jpg";
		}

		return result;
	}

	public static File createNewFileInSDCard(String absolutePath) {
		if (!isExternalStorageMounted()) {
			AppLogger.d("sdcard unavaiable");
			return null;
		}

		File file = new File(absolutePath);
		if (file.exists()) {
			return file;
		} else {
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}

			try {
				if (file.createNewFile()) {
					return file;
				}
			} catch (IOException e) {
				AppLogger.d(e.getMessage());
				return null;
			}
		}

		return null;
	}
	
	public static List<String> getCachePath(){
		List<String> path = new ArrayList<String>();
        if (isExternalStorageMounted()) {
            String thumbnailPath = getSdCardPath() + File.separator + PICTURE_THUMBNAIL;
            String middlePath = getSdCardPath() + File.separator + PICTURE_BMIDDLE;
            String oriPath = getSdCardPath() + File.separator + PICTURE_LARGE;
            String largeAvatarPath = getSdCardPath() + File.separator + AVATAR_LARGE;

            path.add(thumbnailPath);
            path.add(middlePath);
            path.add(oriPath);
            path.add(largeAvatarPath);
        }
        return path;
	}
}
