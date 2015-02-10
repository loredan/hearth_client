package loredan13.hearth_client;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyActivity extends Activity {
    public static final int MSG_RECONFIGURE = 1;

    public static String SERVER_PATH;

    private NumberPicker moodPicker;
    private String[] moods;
    private Messenger messenger;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            messenger = new Messenger(iBinder);
            try {
                Message message = Message.obtain(null, MainService.MSG_BIND_ACTIVITY);
                message.replyTo = new Messenger(new MessageHandler());
                messenger.send(message);
                messenger.send(Message.obtain(null, MainService.MSG_RECONFIGURE));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            try {
                messenger.send(Message.obtain(null, MainService.MSG_UNBIND_ACTIVITY));
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_RECONFIGURE:
                    try {
                        reconfigure(new JSONObject(msg.getData().getString("config")), msg.getData().getString("server_path"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, MainService.class));

        //Mood picker
        moodPicker = new NumberPicker(this);
        moodPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, final int newVal) {
                try {
                    messenger.send(Message.obtain(null, MainService.MSG_UPDATE_MOOD, newVal, 0));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        });

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        mainLayout.addView(moodPicker);

        setContentView(mainLayout);
    }

    @Override
    protected void onStart() {
        bindService(new Intent(this, MainService.class), connection, BIND_AUTO_CREATE);

        super.onStart();
    }

    @Override
    protected void onStop() {
        unbindService(connection);
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getTitle().toString().equals(getString(R.string.set_server_item_title))) {
            SetServerDialogFragment setServerDialog = new SetServerDialogFragment();
            setServerDialog.show(getFragmentManager(), getString(R.string.set_server_dialog_title));
            return true;
        } else if (item.getTitle().toString().equals(getString(R.string.auth_item_title))) {
            AuthorizationDialogFragment authDialog = new AuthorizationDialogFragment();
            authDialog.show(getFragmentManager(), getString(R.string.auth_dialog_title));
            return true;
        }
        return false;
    }

    public void auth(String user, String password) {
        try {
            Message message = Message.obtain(null, MainService.MSG_AUTH, 0, 0);
            Bundle data = new Bundle();
            data.putString("user", user);
            data.putString("password", password);
            message.setData(data);
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void setServer(String path) {
        try {
            Message message = Message.obtain(null, MainService.MSG_SET_SERVER);
            Bundle data = new Bundle();
            data.putString("server_path", path);
            message.setData(data);
            messenger.send(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void reconfigure(JSONObject config, String serverPath) {
        SERVER_PATH = serverPath;

        if (config == null) {
            moods = new String[]{"Not defined"};
            moodPicker.setMinValue(255);
            moodPicker.setMaxValue(255);
            moodPicker.setDisplayedValues(moods);
            moodPicker.setValue(255);
            return;
        }

        JSONObject userConfig;
        try {
            userConfig = config.getJSONObject("user");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        try {
            JSONObject moodConfig = userConfig.getJSONObject("mood");
            JSONArray moodValues = moodConfig.getJSONArray("values");
            JSONArray moodNames = moodConfig.getJSONArray("names");
            if (moodValues.length() == moodNames.length()) {
                moods = new String[moodValues.length()];
                for (int i = 0; i < moodValues.length(); i++) {
                    moods[moodValues.getInt(i)] = moodNames.getString(i);
                }
                moodPicker.setMinValue(0);
                moodPicker.setMaxValue(moods.length - 1);
                moodPicker.setDisplayedValues(moods);
                moodPicker.setValue(moods.length / 2);
            } else {
                moods = new String[]{"Not defined"};
                moodPicker.setMinValue(255);
                moodPicker.setMaxValue(255);
                moodPicker.setDisplayedValues(moods);
                moodPicker.setValue(255);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
