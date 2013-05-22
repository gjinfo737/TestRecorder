package northwoods.testrecorder;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class LogListAdapter extends BaseAdapter {

	List<LogSet> logSets = new ArrayList<LogSet>();
	private Context context;

	public LogListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return logSets.size();
	}

	@Override
	public Object getItem(int position) {
		return logSets.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return ((LogSet) getItem(position)).getView(context);
	}

	public void setItems(List<LogSet> logSets) {
		this.logSets = logSets;
		notifyDataSetChanged();
	}
}
