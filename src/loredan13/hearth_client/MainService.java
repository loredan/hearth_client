package loredan13.hearth_client;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.*;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by loredan13 on 10.02.2015.
 */
public class MainService extends Service {
    public static final int MSG_BIND_ACTIVITY = 1;
    public static final int MSG_UNBIND_ACTIVITY = 2;
    public static final int MSG_RECONFIGURE = 3;
    public static final int MSG_AUTH = 4;
    public static final int MSG_UPDATE_MOOD = 5;
    public static final int MSG_SET_SERVER = 6;

    private static final String PREFERENCES_NAME = "config";
    private JSONObject config;
    private Messenger responseMessenger;
    private SharedPreferences preferences;
    private String token;

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case MSG_BIND_ACTIVITY:
                    responseMessenger = msg.replyTo;
                    break;
                case MSG_UNBIND_ACTIVITY:
                    responseMessenger = null;
                    break;
                case MSG_RECONFIGURE:
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                try {
                                    config = Networking.getConfig();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (config != null) {
                                    preferences.edit().putString("config", config.toString()).apply();
                                    Message message = Message.obtain(null, MyActivity.MSG_RECONFIGURE);
                                    Bundle data = new Bundle();
                                    data.putString("config", config.toString());
                                    data.putString("server_path", Networking.SERVER_PATH);
                                    message.setData(data);
                                    responseMessenger.send(message);
                                } else {
                                    preferences.edit().putString("config", null).apply();
                                    Message message = Message.obtain(null, MyActivity.MSG_RECONFIGURE);
                                    Bundle data = new Bundle();
                                    data.putString("server_path", Networking.SERVER_PATH);
                                    message.setData(data);
                                    responseMessenger.send(message);
                                }
                            } catch (RemoteException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case MSG_AUTH:
                    final String user = msg.getData().getString("user");
                    final String password = msg.getData().getString("password");
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                token = Networking.auth(user, password);
                                preferences.edit().putString("token", token).apply();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case MSG_UPDATE_MOOD:
                    final int mood = msg.arg1;
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Networking.updateMood(token, mood);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    break;
                case MSG_SET_SERVER:
                    Networking.setServer(msg.getData().getString("server_path"));
                    preferences.edit().putString("server_path", msg.getData().getString("server_path")).apply();
                    sendMessage(Message.obtain(null, MSG_RECONFIGURE));
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String configString = preferences.getString("config", null);
        try {
            config = configString != null ? new JSONObject(configString) : null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        token = preferences.getString("token", null);
        Networking.setServer(preferences.getString("server_path", null));
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Messenger messenger = new Messenger(new MessageHandler());
        return messenger.getBinder();
    }
}
