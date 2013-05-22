package northwoods.testrecorder;

import northwoods.testrecorder.R.id;
import northwoods.testrecorder.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HTMLView extends Activity {

	public static final String HTMLFILE = null;
	public static String HTML = "";
	private WebView webview;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(layout.html_layout);
		webview = (WebView) findViewById(id.webView);
		String mime = "text/html";
		String encoding = "utf-8";
		webview.loadDataWithBaseURL(null, HTML, mime, encoding, null);
	}

	@Override
	protected void onPause() {
		super.onPause();
		finish();
	}
}
