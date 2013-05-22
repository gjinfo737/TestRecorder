package northwoods.testrecorder;

import northwoods.testrecorder.TestRecorderHelper.LogLevel;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.View.OnClickListener;

public interface ITestRecorderView {

	public Context getApplicationContext();

	public PackageManager getPackageManager();

	public SharedPreferences getSharedPreferences(String name, int mode);

	public void setClickListenerOnView(int id, OnClickListener listener);

	public void setTextViewText(int id, String text);

	public void setViewEnabled(int id, boolean enabled);

	public String getEnteredContextName();

	public String getEnteredFolderName();

	public int getEnteredSnapshotLength();

	public LogLevel getEnteredLevel();

	public void setEnteredContextName(String contextName);

	public void setEnteredFolderName(String folderName);

	public void setEnteredSnapshotLength(int n);

	public void setEnteredLevel(LogLevel level);

	public Activity getActivity();
}
