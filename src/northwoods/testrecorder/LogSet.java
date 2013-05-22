package northwoods.testrecorder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

public class LogSet {

	public Date date = new Date(0);
	public List<LogLine> logLines = new ArrayList<LogLine>();

	public View getView(Context context) {
		TextView tvtitle = new TextView(context);
		tvtitle.setText("\n" + TestRecorderHelper.LOG_SimpleDateFormat.format(date));
		return tvtitle;

		// List<LogLine> logLines = logLines;
		// for (LogLine logLine : logLines) {
		// TextView tv_line = new TextView(context);
		// tv_line.setText(logLine.line);
		// tv_line.setTextColor(TestRecorderActivityPresenter.colorMap.get(logLine.level));
		// linlay.addView(tv_line);
		// }
	}
}
