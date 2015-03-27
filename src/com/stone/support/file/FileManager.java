package com.stone.support.file;

import java.io.File;

import com.stone.black.R;
import com.stone.support.utils.GlobalContext;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Environment;
import android.widget.Toast;

public class FileManager {

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
}
