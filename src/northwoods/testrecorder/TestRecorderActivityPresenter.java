package northwoods.testrecorder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import northwoods.testrecorder.TestRecorderHelper.LogLevel;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class TestRecorderActivityPresenter {
	public static int NOTIFICATION_ID = 1132290948;
	public static String SPKEY_SNAP_LENGTH = "SPKEY_SNAP_LENGTH";
	public static String SPKEY_FOLDER_NAME = "SPKEY_FOLDER_NAME";
	public static String SPKEY_CONTEXT_NAME = "SPKEY_CONTEXT_NAME";
	public static String SPKEY_LEVEL = "SPKEY_LEVEL";
	public static final String SHARED_PREFERENCE_NAME = TestRecorderActivity.class.getName() + ".prefs";
	public static final String B_ACTION = "northwoods.testrecorder.TestRecorderBroadCastReceiver";
	private LogListAdapter adapter;
	public static final Map<LogLevel, Integer> colorMap = new HashMap<TestRecorderHelper.LogLevel, Integer>() {
		private static final long serialVersionUID = 1L;
		{
			put(LogLevel.ERROR, Color.RED);
			put(LogLevel.WARNING, Color.YELLOW);
			put(LogLevel.INFO, Color.GREEN);
			put(LogLevel.DEBUG, Color.BLUE);
			put(LogLevel.VERBOSE, Color.WHITE);
		}
	};
	public ITestRecorderView mView;
	private final ListView listView;

	public TestRecorderActivityPresenter(ITestRecorderView c, Context context, ListView listView) {
		this.mView = c;
		this.listView = listView;
		this.adapter = new LogListAdapter(context);
		listView.setAdapter(adapter);
	}

	public void onResumeInterface() {
		loadAllPrefs();

		mView.setViewEnabled(R.id.editText_contextName, false);

		PackageInfo pInfo;
		try {
			pInfo = mView.getPackageManager().getPackageInfo(mView.getApplicationContext().getPackageName(), 0);

			mView.setTextViewText(R.id.textView_version, "Ver." + pInfo.versionName + "." + pInfo.versionCode);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		mView.setClickListenerOnView(R.id.button_reload_logs, new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleReloadLogsBtn();
			}
		});
		mView.setClickListenerOnView(R.id.button_reload_logs, new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleReloadLogsBtn();
			}
		});
		mView.setClickListenerOnView(R.id.button_startTracking, new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleStartTrackingBtn();
			}
		});
		mView.setClickListenerOnView(R.id.button_stopTracking, new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleStopTrackingBtn();
			}
		});
		mView.setClickListenerOnView(R.id.button_savePrefs, new OnClickListener() {
			@Override
			public void onClick(View v) {
				handleSavePrefsBtn();
			}
		});
		reloadLogs();
	}

	public void reloadLogs() {
		List<LogSet> logSets = TestRecorderHelper.getAllJSONLogFiles();
		adapter.setItems(logSets);
		// LinearLayout linlay = mView.getLogsLinearLayout();
		// linlay.removeAllViews();
		// for (LogSet set : logSets) {
		// TextView tvtitle = new TextView(mView.getApplicationContext());
		// tvtitle.setText("\n" +
		// TestRecorderHelper.LOG_SimpleDateFormat.format(set.date));
		// linlay.addView(tvtitle);
		// List<LogLine> logLines = set.logLines;
		// for (LogLine logLine : logLines) {
		// TextView tv_line = new TextView(mView.getApplicationContext());
		// tv_line.setText(logLine.line);
		// tv_line.setTextColor(colorMap.get(logLine.level));
		// linlay.addView(tv_line);
		// }
		// }
	}

	public void handleReloadLogsBtn() {
		reloadLogs();
		Toast.makeText(mView.getApplicationContext(), "Done reloading logs", Toast.LENGTH_SHORT).show();
	}

	public void handleStartTrackingBtn() {
		startTracking(mView.getApplicationContext());
	}

	public void handleStopTrackingBtn() {
		stopTracking(mView.getApplicationContext());
	}

	public void handleSavePrefsBtn() {
		stopTracking(mView.getApplicationContext());
		saveAllPrefs();
	}

	public void startTracking(Context context) {

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(NOTIFICATION_ID, getNotification(context));
	}

	public void stopTracking(Context context) {

		// context.unregisterReceiver(bReceiver);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(NOTIFICATION_ID);
	}

	void takeSnapShot(Context context) {
		stopTracking(context);
		TestRecorderHelper.takeSnapShot(context.getApplicationContext().getPackageName(), TestRecorderHelper.snapshotLength);
		Toast.makeText(context, "Snapshot taken.", Toast.LENGTH_SHORT).show();
		startTracking(context);
	}

	public void loadAllPrefs() {
		SharedPreferences sharedPrefs = loadPreferences();
		TestRecorderHelper.contextName = sharedPrefs.getString(SPKEY_CONTEXT_NAME, TestRecorderHelper.DEFAULT_CONTEXT_NAME);
		String ssl = sharedPrefs.getString(SPKEY_SNAP_LENGTH, TestRecorderHelper.DEFAULT_SNAPSHOT_LENGTH + "");
		TestRecorderHelper.snapshotLength = Integer.parseInt(ssl);
		TestRecorderHelper.folderName = sharedPrefs.getString(SPKEY_FOLDER_NAME, TestRecorderHelper.DEFAULT_FOLDER_NAME);
		TestRecorderHelper.logLevel = LogLevel.valueOf(sharedPrefs.getString(SPKEY_LEVEL, TestRecorderHelper.DEFAULT_LEVEL.toString()));

		mView.setEnteredContextName(TestRecorderHelper.contextName);
		mView.setEnteredFolderName(TestRecorderHelper.folderName);
		mView.setEnteredLevel(TestRecorderHelper.logLevel);
		mView.setEnteredSnapshotLength(TestRecorderHelper.snapshotLength);
	}

	public void saveAllPrefs() {

		TestRecorderHelper.contextName = mView.getEnteredContextName();
		TestRecorderHelper.folderName = mView.getEnteredFolderName();
		TestRecorderHelper.logLevel = mView.getEnteredLevel();
		TestRecorderHelper.snapshotLength = mView.getEnteredSnapshotLength();

		savePreferences(SPKEY_CONTEXT_NAME, TestRecorderHelper.contextName);
		savePreferences(SPKEY_FOLDER_NAME, TestRecorderHelper.folderName);
		savePreferences(SPKEY_LEVEL, TestRecorderHelper.logLevel.toString());
		savePreferences(SPKEY_SNAP_LENGTH, TestRecorderHelper.snapshotLength + "");
	}

	Notification getNotification(Context context) {
		CharSequence tickerText = "";
		long when = System.currentTimeMillis();

		CharSequence contentTitle = "Take Logcat Snapshot";

		CharSequence contentText = TestRecorderHelper.snapshotLength + " - " + TestRecorderHelper.folderName;

		Intent notificationIntent = new Intent();
		notificationIntent.setAction(B_ACTION);

		PendingIntent contentIntent = PendingIntent.getBroadcast(context, 0, notificationIntent, 0);

		Notification notification = new Notification(R.drawable.ic_launcher, tickerText, when);
		notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);

		notification.flags = Notification.FLAG_NO_CLEAR | Notification.FLAG_INSISTENT | Notification.FLAG_ONGOING_EVENT;

		return notification;
	}

	public void savePreferences(String key, String value) {
		SharedPreferences sharedPreferences = mView.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public SharedPreferences loadPreferences() {
		return mView.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
	}
}
