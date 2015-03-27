package com.stone.support.crashmanager;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.UUID;

import android.util.Log;

import com.stone.support.file.FileManager;
import com.stone.support.utils.SysConsts;

public class ExceptionHandler implements Thread.UncaughtExceptionHandler {

	private Thread.UncaughtExceptionHandler previousHandler;

	public ExceptionHandler(Thread.UncaughtExceptionHandler handler) {
		this.previousHandler = handler;
	}

	@Override
	public void uncaughtException(Thread thread, Throwable exception) {

		Log.d(SysConsts.SYSTEM_TAG, "An uncaught exception has been caught");

		final Date now = new Date();
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		exception.printStackTrace();

		try {
			String logDir = FileManager.getLogDir();
			String filename = UUID.randomUUID().toString();
			String path = logDir + File.separator + filename + ".stacktrack";

			BufferedWriter write = new BufferedWriter(new FileWriter(path));
			write.write("Package: " + CrashManagerConstants.APP_PACKAGE + "\n");
			write.write("Version: " + CrashManagerConstants.APP_VERSION + "\n");
			write.write("Android: " + CrashManagerConstants.ANDROID_VERSION
					+ "\n");
			write.write("Manufacturer: "
					+ CrashManagerConstants.PHONE_MANUFACTURER + "\n");
			write.write("Model: " + CrashManagerConstants.PHONE_MODEL + "\n");
			write.write("Date: " + now + "\n");
			write.write("\n");
			write.write(result.toString());
			write.flush();
			write.close();

		} catch (Exception antoher) {

		} finally {
			previousHandler.uncaughtException(thread, exception);
		}

	}
}
