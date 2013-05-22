package northwoods.testrecorder;

import static northwoods.testrecorder.TestRecorderHelper.DELIM;
import static northwoods.testrecorder.TestRecorderHelper.FILENAME;
import static northwoods.testrecorder.TestRecorderHelper.LOG_SimpleDateFormat;
import static northwoods.testrecorder.TestRecorderHelper.RELATIVEPATH;
import static northwoods.testrecorder.TestRecorderHelper.folderName;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class LogSet {

	public Date date = new Date(0);
	public List<LogLine> logLines = new ArrayList<LogLine>();

	public LogSet(Date date) {
		this.date = date;
	}

	public View getView(Activity activity) {
		TextView tvtitle = new TextView(activity.getApplicationContext());
		tvtitle.setText("\n" + TestRecorderHelper.LOG_SimpleDateFormat.format(date));
		setClicker(activity, tvtitle);
		return tvtitle;
	}

	private void setClicker(final Activity activity, final TextView tvtitle) {
		tvtitle.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				File htmlfile = new File(getHTMLPath());
				if (htmlfile != null) {
					if (htmlfile.exists()) {
						HTMLView.HTML = new String(TestRecorderHelper.readFileToBytes(htmlfile));
						Intent intent = new Intent(activity, HTMLView.class);
						activity.startActivityForResult(intent, 10);
					}
				}
			}
		});
	}

	public String getRelativePath() {
		String relativePath = RELATIVEPATH + File.separator + folderName + File.separator;
		return relativePath;
	}

	public String getHTMLPath() {
		String fileName = getFileName();
		String htmlpath = getRelativePath() + fileName + ".html";
		return htmlpath;
	}

	public String getJSONPath() {
		String fileName = getFileName();
		String jsonpath = getRelativePath() + fileName + ".json";
		return jsonpath;
	}

	public String getFileName() {
		return FILENAME + DELIM + LOG_SimpleDateFormat.format(date) + DELIM;
	}
}
