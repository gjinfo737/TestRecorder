package northwoods.testrecorder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Environment;
import android.util.Log;

public class TestRecorderHelper {

	private static final String COMMAND = "logcat -d";
	public static final String RELATIVEPATH = Environment.getExternalStorageDirectory().getAbsolutePath();
	public static final String DELIM = "_";
	public static final String FILENAME = "log";
	public static final String DEFAULT_CONTEXT_NAME = "";
	public static final String DEFAULT_FOLDER_NAME = "logcats";
	public static final String JO_NAME_LINES = "lines";
	public static final LogLevel DEFAULT_LEVEL = LogLevel.VERBOSE;
	public static final int DEFAULT_SNAPSHOT_LENGTH = 60;

	public static String folderName = DEFAULT_FOLDER_NAME;
	public static String contextName = DEFAULT_CONTEXT_NAME;
	public static LogLevel logLevel = DEFAULT_LEVEL;
	public static int snapshotLength = DEFAULT_SNAPSHOT_LENGTH;
	public static final SimpleDateFormat LOG_SimpleDateFormat = new SimpleDateFormat("MM-dd-yyy-HH.mm.ss");

	public static enum LogLevel {
		VERBOSE, DEBUG, INFO, WARNING, ERROR
	}

	public static void takeSnapShot(final String contextName, final int numberOfLines) {
		Thread writeLogsThread = new Thread(new Runnable() {
			public void run() {
				//

				Date date = new Date();
				LogSet logSet = new LogSet(date);
				String relativePath = logSet.getRelativePath();
				String htmlpath = logSet.getHTMLPath();
				String jsonpath = logSet.getJSONPath();
				File dir = new File(relativePath);
				File json_file = new File(jsonpath);
				File html_file = new File(htmlpath);
				if (!dir.exists()) {
					if (dir.mkdirs()) {
					} else {
					}
				}
				if (json_file.exists()) {
					json_file.delete();
				} else {
					try {
						json_file.createNewFile();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
				if (html_file.exists()) {
					html_file.delete();
				} else {
					try {
						html_file.createNewFile();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
				//
				//
				writeLogsToJSON(contextName, json_file, logLevel, numberOfLines);
				writeLogsToHTML(contextName, html_file, logLevel, numberOfLines);
			}
		});
		writeLogsThread.start();
	}

	// private static final String[] classes = new String[] { "verbose",
	// "debug",
	// "info", "warning", "error" };

	public static List<LogLine> getLogs(final String contextName, final File file, final LogLevel level, final int numberOfLines) {
		Process process;
		List<LogLine> logStrings = new ArrayList<LogLine>();
		try {
			process = Runtime.getRuntime().exec(COMMAND);

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			String line;

			while ((line = bufferedReader.readLine()) != null) {

				// && !line.contains("PackageManager")
				// && !line.contains("ActivityManager")
				// && !line.contains("ActivityThread")
				// && !line.contains("dalvikvm")
				// && !line.contains("System.out")
				// && !line.contains("WindowManager")
				if (((line.contains(contextName) && line.indexOf(contextName) != -1) || contextName.equals(""))) {

					String levelSTR = line.substring(0, 2);

					LogLine logLine = new LogLine();

					if (levelSTR.contains("E/")) {
						logLine.level = LogLevel.ERROR;
					}
					if (levelSTR.contains("W/")) {
						logLine.level = LogLevel.WARNING;
					}
					if (levelSTR.contains("I/")) {
						logLine.level = LogLevel.INFO;
					}
					if (levelSTR.contains("D/")) {
						logLine.level = LogLevel.DEBUG;
					}
					if (levelSTR.contains("V/")) {
						logLine.level = LogLevel.VERBOSE;
					}

					boolean writeLine = false;
					switch (level) {
					case DEBUG:
						if (logLine.level != LogLevel.VERBOSE) {
							writeLine = true;
						}
						break;
					case INFO:
						if (logLine.level != LogLevel.VERBOSE && logLine.level != LogLevel.DEBUG) {
							writeLine = true;
						}
						break;
					case WARNING:
						if (logLine.level != LogLevel.VERBOSE && logLine.level != LogLevel.DEBUG && logLine.level != LogLevel.INFO) {
							writeLine = true;
						}
						break;
					case ERROR:
						if (logLine.level != LogLevel.VERBOSE && logLine.level != LogLevel.DEBUG && logLine.level != LogLevel.INFO && logLine.level != LogLevel.WARNING) {
							writeLine = true;
						}
						break;
					case VERBOSE:
					default:
						writeLine = true;
						break;
					}
					if (writeLine) {
						logLine.line = line;
						logStrings.add(logLine);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return logStrings;
	}

	public static void writeLogsToHTML(final String contextName, final File file, final LogLevel level, final int numberOfLines) {

		Thread writeLogsThread = new Thread(new Runnable() {

			public void run() {

				List<LogLine> logLines = getLogs(contextName, file, level, numberOfLines);

				int skip = 0;
				if (logLines.size() > numberOfLines) {
					skip = logLines.size() - numberOfLines;
				}

				String logString = "<html>";
				logString += "<head><style type=\"text/css\">body {background-color:#000; color:#fff;}";
				logString += "." + LogLevel.VERBOSE.toString().toLowerCase() + "{color:#fff;}";
				logString += "." + LogLevel.DEBUG.toString().toLowerCase() + "{color:blue;}";
				logString += "." + LogLevel.INFO.toString().toLowerCase() + "{color:green;}";
				logString += "." + LogLevel.WARNING.toString().toLowerCase() + "{color:yellow;}";
				logString += "." + LogLevel.ERROR.toString().toLowerCase() + "{color:red;}";
				logString += "</style></head>";
				logString += "<body>";
				int count = 1;
				for (int i = skip; i < logLines.size(); i++) {
					String line = logLines.get(i).line;
					String styleClassSTR = logLines.get(i).level.toString().toLowerCase();
					logString += "<br/>" + count + ": ";
					line = "<span class=\"" + styleClassSTR + "\">" + line + "</span>";
					logString += line;
					count++;
				}
				logString += "</body></html>";
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				writeBytesToFile(file.getAbsolutePath(), logString);
				Log.i("", "writeLogs HTML success");

			}
		});
		writeLogsThread.start();

	}

	public static void writeLogsToJSON(final String contextName, final File file, final LogLevel level, final int numberOfLines) {

		Thread writeLogsThread = new Thread(new Runnable() {

			public void run() {

				List<LogLine> logLines = getLogs(contextName, file, level, numberOfLines);

				int skip = 0;
				if (logLines.size() > numberOfLines) {
					skip = logLines.size() - numberOfLines;
				}
				String logString = "{";
				logString += "\"" + JO_NAME_LINES + "\":[";
				for (int i = skip; i < logLines.size(); i++) {
					logString += "{";
					logString += "\"line\":";
					logString += "\"" + logLines.get(i).line + "\"";
					logString += ",";
					logString += "\"level\":";
					logString += "\"" + logLines.get(i).level.toString().toLowerCase() + "\"";
					logString += "}";
					if (i < logLines.size() - 1) {
						logString += ",\n";
					}
				}
				logString += "]";
				logString += "}";
				try {
					if (!file.exists()) {
						file.createNewFile();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}

				writeBytesToFile(file.getAbsolutePath(), logString);

			}
		});
		writeLogsThread.start();

	}

	public static void writeBytesToFile(String strFilePath, String contents) {
		writeBytesToFile(strFilePath, contents.getBytes());
	}

	public static void writeBytesToFile(String strFilePath, byte[] byte_array) {
		try {
			File f = new File(strFilePath);
			if (!f.exists()) {
				Log.e("writeBytesToFile", " File does not exist: " + strFilePath);
				return;
			}

			FileOutputStream fos = new FileOutputStream(strFilePath, true);
			fos.write(byte_array);
			fos.close();

		} catch (FileNotFoundException ex) {
			Log.e("", "FileNotFoundException : " + ex);
		} catch (IOException ioe) {
			Log.e("", "IOException : " + ioe);
		}
	}

	public static byte[] readFileToBytes(File file) {

		// Map<String, byte[]> b_arrays = new HashMap<String, byte[]>();

		int headerLength = 46;
		int bodyLength = (int) (file.length() - headerLength);

		if (bodyLength < 0) {
			bodyLength = 10;
		}

		return toByteArray(file, 0, (int) file.length());
	}

	public static byte[] toByteArray(File file, int offset, int length) {

		byte[] array = new byte[length];
		InputStream in;
		try {
			in = new FileInputStream(file);
			// long skipped = in.skip(offset);
			in.skip(offset);
			// Log.i("", "skipped: " + skipped + "  offset: " + offset);
			in.read(array);
			in.close();
		} catch (IOException e) {
			Log.e("WaveFileConcatenater.toByteArray", "IO exception.  Unable to read " + file.getAbsolutePath());
			e.printStackTrace();
		}
		return array;
	}

	public static boolean deleteFileOrDirectory(File file) throws FileNotFoundException {
		if (!file.exists())
			throw new FileNotFoundException(file.getAbsolutePath());
		boolean ret = true;
		if (file.isDirectory()) {
			for (File f : file.listFiles()) {
				Log.i("deleteFileOrDirectory", "deleting " + f.getPath());
				ret = ret && deleteFileOrDirectory(f);
			}
		}
		return ret && file.delete();
	}

	public static List<LogSet> getAllJSONLogFiles() {

		List<LogSet> logSets = new ArrayList<LogSet>();
		String relativePath = RELATIVEPATH + File.separator + folderName + File.separator;
		File dir = new File(relativePath);

		if (dir.exists()) {
			if (dir.isDirectory()) {
				File[] files = dir.listFiles();
				for (int i = 0; i < files.length; i++) {
					if (!files[i].isDirectory()) {
						String logFileName = files[i].getName();
						if (logFileName.substring(logFileName.length() - 5).equals(".json")) {
							int index1 = logFileName.indexOf(DELIM);
							int index2 = logFileName.substring(index1 + 1).indexOf(DELIM) + index1 + 1;
							if (index1 != -1 && index2 != -1) {
								try {
									String dateString = logFileName.substring(index1 + 1, index2);
									Date d = LOG_SimpleDateFormat.parse(dateString);
									//
									String contents = new String(readFileToBytes(files[i]));
									try {
										JSONObject allJO = new JSONObject(contents);
										JSONArray ja = allJO.getJSONArray(JO_NAME_LINES);
										LogSet logSet = new LogSet(d);
										for (int j = 0; j < ja.length(); j++) {
											LogLine logLine = new LogLine();
											JSONObject jo = ja.getJSONObject(j);
											logLine.line = jo.getString("line");
											String levelstring = jo.getString("level").toUpperCase();
											logLine.level = LogLevel.valueOf(levelstring);
											logSet.logLines.add(logLine);
										}
										logSets.add(logSet);
									} catch (JSONException e) {
										e.printStackTrace();
									}
								} catch (ParseException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
		}

		List<LogSet> orderedLogSets = new ArrayList<LogSet>();
		while (logSets.size() > 0) {
			Date oldestDate = new Date();
			int oldestIndex = -1;
			for (int i = 0; i < logSets.size(); i++) {
				if (logSets.get(i).date.compareTo(oldestDate) <= 0) {
					oldestDate = logSets.get(i).date;
					oldestIndex = i;
				}
			}
			orderedLogSets.add(logSets.get(oldestIndex));
			logSets.remove(oldestIndex);
		}
		return orderedLogSets;
	}
}
