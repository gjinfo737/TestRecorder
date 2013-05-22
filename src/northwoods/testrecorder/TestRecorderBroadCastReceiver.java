package northwoods.testrecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class TestRecorderBroadCastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
	TestRecorderActivity.takeSnapShot(context);
    }

}
