package northwoods.testrecorder;

import northwoods.testrecorder.TestRecorderHelper.LogLevel;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

public class TestRecorderActivity extends Activity implements ITestRecorderView {
    /** Called when the activity is first created. */

    private static TestRecorderActivityPresenter mPresenter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.main);
	mPresenter = new TestRecorderActivityPresenter(this);

    }

    @Override
    protected void onResume() {

	mPresenter.onResumeInterface();
	super.onResume();
    }

    // ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    @Override
    public String getEnteredContextName() {
	return ((EditText) findViewById(R.id.editText_contextName)).getText()
		.toString();
    }

    @Override
    public String getEnteredFolderName() {
	return ((EditText) findViewById(R.id.editText_folderName)).getText()
		.toString();
    }

    @Override
    public int getEnteredSnapshotLength() {
	return Integer
		.parseInt(((EditText) findViewById(R.id.editText_snapShotLength))
			.getText().toString());
    }

    @Override
    public LogLevel getEnteredLevel() {
	RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
	int id = rg.getCheckedRadioButtonId();
	if (id == R.id.radiod) {
	    return LogLevel.DEBUG;
	} else if (id == R.id.radioi) {
	    return LogLevel.INFO;
	} else if (id == R.id.radiow) {
	    return LogLevel.WARNING;
	} else if (id == R.id.radioe) {
	    return LogLevel.ERROR;
	}
	return LogLevel.VERBOSE;
    }

    @Override
    public void setEnteredContextName(String contextName) {
	((EditText) findViewById(R.id.editText_contextName))
		.setText(contextName);
    }

    @Override
    public void setEnteredFolderName(String folderName) {
	((EditText) findViewById(R.id.editText_folderName)).setText(folderName);
    }

    @Override
    public void setEnteredSnapshotLength(int n) {
	((EditText) findViewById(R.id.editText_snapShotLength)).setText(n + "");
    }

    @Override
    public void setEnteredLevel(LogLevel level) {
	RadioGroup rg = (RadioGroup) findViewById(R.id.radioGroup1);
	int id = R.id.radiov;
	switch (level) {
	case DEBUG:
	    id = R.id.radiod;
	    break;
	case INFO:
	    id = R.id.radioi;
	    break;
	case WARNING:
	    id = R.id.radiow;
	    break;
	case ERROR:
	    id = R.id.radioe;
	    break;
	case VERBOSE:
	default:
	    id = R.id.radiov;
	    break;
	}
	rg.check(id);
    }

    @Override
    public void setClickListenerOnView(int btnId, OnClickListener listener) {
	((Button) findViewById(btnId)).setOnClickListener(listener);
    }

    @Override
    public void setTextViewText(int id, String text) {
	((TextView) findViewById(id)).setText(text);
    }

    @Override
    public void setViewEnabled(int id, boolean enabled) {
	findViewById(id).setEnabled(enabled);
    }

    @Override
    public LinearLayout getLogsLinearLayout() {
	return (LinearLayout) findViewById(R.id.logs_linear_layout);
    }

    public static void takeSnapShot(Context context) {
	mPresenter.takeSnapShot(context);
    }
}