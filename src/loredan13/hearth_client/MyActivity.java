package loredan13.hearth_client;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyActivity extends Activity {
    private NumberPicker moodPicker;
    private String[] moods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Networking.init(getPreferences(MODE_PRIVATE), this);

        //Mood picker
        moodPicker = new NumberPicker(this);
        moodPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, final int newVal) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Networking.updateMood(newVal);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        update();

        LinearLayout mainLayout = new LinearLayout(this);
        mainLayout.setOrientation(LinearLayout.VERTICAL);

        mainLayout.addView(moodPicker);

        setContentView(mainLayout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem setServerItem = menu.add(R.string.set_server_item_title);
        setServerItem.setIcon(R.drawable.ic_action_web_site);

        MenuItem authItem = menu.add(R.string.auth_item_title);
        authItem.setIcon(R.drawable.ic_action_accounts);

        return true;
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

    public void update() {
        JSONObject userConfig;
        try {
            userConfig = Networking.CONFIG.getJSONObject("user");
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
            } else {
                moods = new String[]{"Not defined"};
                moodPicker.setMinValue(-1);
                moodPicker.setMaxValue(-1);
                moodPicker.setDisplayedValues(moods);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
