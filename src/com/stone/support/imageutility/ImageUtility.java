package com.stone.support.imageutility;

import java.io.File;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.stone.support.error.WeiboException;
import com.stone.support.file.FileDownloaderHttpHelper;
import com.stone.support.file.FileLocationMethod;
import com.stone.support.file.FileManager;
import com.stone.support.http.HttpUtility;
import com.stone.support.settinghelper.SettingUtility;

public class ImageUtility {

	public static boolean getBitmapFromNetWork(String url, String path,
			FileDownloaderHttpHelper.DownloadListener downloadListener) {
		for (int i = 0; i < 3; i++) {
			if (HttpUtility.getInstance().executeDownloadTask(url, path,
					downloadListener)) {
				return true;
			}
			new File(path).delete();
		}

		return false;
	}

	public static Bitmap getRoundedCornerPic(String filePath, int reqWidth,
			int reqHeight, int cornerRadius) {
		try {
			if (!FileManager.isExternalStorageMounted()) {
				return null;
			}

			if (!filePath.endsWith(".jpg") && filePath.endsWith(".gif")
					&& filePath.endsWith(".png")) {
				filePath = filePath + ".jpg";
			}

			boolean fileExist = new File(filePath).exists();

			if (!fileExist && !SettingUtility.isEnablePic()) {
				return null;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			options.inInputShareable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

			if (bitmap == null) {
				// this picture is broken, so delete it
				new File(filePath).delete();
				return null;
			}

			if (cornerRadius > 0) {
				int[] size = calcResize(bitmap.getWidth(), bitmap.getHeight(),
						reqWidth, reqHeight);
				if (size[0] > 0 && size[1] > 0) {
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
							size[0], size[1], false);
					if (scaledBitmap != bitmap) {
						bitmap.recycle();
						bitmap = scaledBitmap;
					}
				}

				Bitmap roundedBitmap = ImageEditUtility.getRoundedCornerBitmap(
						bitmap, cornerRadius);
				if (roundedBitmap != bitmap) {
					bitmap.recycle();
					bitmap = roundedBitmap;
				}
			}
			return bitmap;
		} catch (OutOfMemoryError ignored) {
			ignored.printStackTrace();
			return null;
		}
	}

	public static Bitmap getRoundedCornerPic(String url, int reqWidth,
			int reqHeight, FileLocationMethod method,
			FileDownloaderHttpHelper.DownloadListener downloadListener)
			throws WeiboException {
		try {
			if (!FileManager.isExternalStorageMounted()) {
				return null;
			}

			String filePath = FileManager.getFilePathFromUrl(url, method);
			if (!filePath.endsWith(".jpg") && !filePath.endsWith(".gif")) {
				filePath = filePath + ".jpg";
			}

			boolean fileExist = new File(filePath).exists();

			if (!fileExist && !SettingUtility.isEnablePic()) {
				return null;
			}

			if (!fileExist) {
				boolean result = getBitmapFromNetWork(url, filePath,
						downloadListener);
				if (!result) {
					return null;
				}
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			options.inSampleSize = calculateInSampleSize(options, reqWidth,
					reqHeight);
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			options.inInputShareable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

			if (bitmap == null) {
				// this picture is broken, so delete it
				new File(filePath).delete();
				return null;
			}

			int[] size = calcResize(bitmap.getWidth(), bitmap.getHeight(),
					reqWidth, reqHeight);
			if (size[0] > 0 && size[1] > 0) {
				Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
						size[0], size[1], true);
				if (scaledBitmap != bitmap) {
					bitmap.recycle();
					bitmap = scaledBitmap;
				}
			}

			Bitmap roundedBitmap = ImageEditUtility
					.getRoundedCornerBitmap(bitmap);
			if (roundedBitmap != bitmap) {
				bitmap.recycle();
				bitmap = roundedBitmap;
			}

			return bitmap;
		} catch (OutOfMemoryError ignored) {
			ignored.printStackTrace();
			return null;
		}

	}

	private static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (height > reqHeight && reqHeight != 0) {
				inSampleSize = (int) Math.floor((double) height
						/ (double) reqHeight);
			}

			int tmp = 0;

			if (width > reqWidth && reqWidth != 0) {
				tmp = (int) Math.floor((double) width / (double) reqWidth);
			}

			inSampleSize = Math.max(inSampleSize, tmp);
		}

		int roundedSize;
		if (inSampleSize <= 8) {
			roundedSize = 1;
			while (roundedSize < inSampleSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (inSampleSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int[] calcResize(int actualWidth, int actualHeight,
			int reqWidth, int reqHeight) {
		int height = actualHeight;
		int width = actualWidth;

		float betweenWidth = ((float) reqWidth) / (float) actualWidth;
		float betweenHeight = ((float) reqHeight) / (float) actualHeight;

		float min = Math.min(betweenHeight, betweenWidth);

		height = (int) (min * actualHeight);
		width = (int) (min * actualWidth);

		return new int[] { width, height };
	}

	public static boolean isThisPictureGif(String url) {
		return !TextUtils.isEmpty(url) && url.endsWith(".gif");
	}

	public static Bitmap readNormalPic(String filePath, int reqWidth,
			int reqHeight) {
		try {
			if (!FileManager.isExternalStorageMounted()) {
				return null;
			}

			if (!filePath.endsWith(".jpg") && !filePath.endsWith(".gif")
					&& !filePath.endsWith(".png"))
				filePath = filePath + ".jpg";

			boolean fileExist = new File(filePath).exists();
			if (!fileExist && !SettingUtility.isEnablePic()) {
				return null;
			}

			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(filePath, options);

			if (reqHeight > 0 && reqWidth > 0) {
				options.inSampleSize = calculateInSampleSize(options, reqWidth,
						reqHeight);
			}
			options.inJustDecodeBounds = false;
			options.inPurgeable = true;
			options.inInputShareable = true;

			Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

			if (bitmap == null) {
				// this picture is broken, so delete it
				new File(filePath).delete();
				return null;
			}

			if (reqHeight > 0 && reqWidth > 0) {
				int[] size = calcResize(bitmap.getWidth(), bitmap.getHeight(),
						reqWidth, reqHeight);
				if (size[0] > 0 && size[1] > 0) {
					Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
							size[0], size[1], true);
					if (scaledBitmap != bitmap) {
						bitmap.recycle();
						bitmap = scaledBitmap;
					}
				}
			}

			return bitmap;
		} catch (OutOfMemoryError ignored) {
			ignored.printStackTrace();
			return null;
		}
	}
}
