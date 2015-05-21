package pl.modrakowski.compasnetguru.util;

import android.util.Log;
import pl.modrakowski.compasnetguru.BuildConfig;

/**
 * User: jacek
 * Date: 21/05/15
 * Time: 11:07
 * modrakowski.pl/android
 */
public class Logger {
	public static final boolean isEnabled = BuildConfig.DEBUG;
	public static final String TAG = "compas_netguru";

	public static void l(boolean message) {
		l(String.valueOf(message));
	}

	public static void l(int message) {
		l(String.valueOf(message));
	}

	public static void l(String message) {
		if (isEnabled) {
			Log.d(TAG, message);
		}
	}
}
