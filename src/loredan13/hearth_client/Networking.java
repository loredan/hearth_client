package loredan13.hearth_client;

import android.content.SharedPreferences;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by loredan13 on 02.02.2015.
 */
public class Networking {
    public static final String PORT = "12592";
    public static final String AUTH_PATH = "/api/auth";
    public static final String MOOD_PATH = "/api/path";
    public static final String CONFIG_PATH = "/api/config";
    public static final String STATE_PATH = "/api/state";
    public static final String LOCATION_PATH = "/api/location";

    private static String TOKEN;
    private static String SERVER_PATH;

    private static SharedPreferences preferences;

    public static void init(SharedPreferences preferences) {
        Networking.preferences = preferences;
        SERVER_PATH = preferences.getString("server_path", null);
        TOKEN = preferences.getString("token", null);
    }

    public static void setServer(String path) {
        SERVER_PATH = path + ":" + PORT;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("server_path", SERVER_PATH);
        editor.commit();
    }

    public static void auth(String user, String password) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_PATH + AUTH_PATH).openConnection();
        connection.setDoOutput(true);
        String request = new JSONStringer()
                .object()
                .key("user")
                .value(user)
                .key("password")
                .value(password)
                .endObject()
                .toString();
        connection.getOutputStream().write(request.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while (true) {
            String temp = reader.readLine();
            if (temp == null) {
                break;
            }
            builder.append(temp);
        }
        JSONObject response = new JSONObject(builder.toString());
        TOKEN = response.getString("token");
    }

    public static void updateMood(int mood) throws Exception {
        if (TOKEN == null) {
        throw new Exception("Token not set");
                }

        HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_PATH + MOOD_PATH + "?mood=" + mood).openConnection();

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
            case 403:
                throw new Exception("403 Not Authorized");
        }
    }

    public static void updateState(int state) throws Exception {
        if (TOKEN == null) {
            throw new Exception("Token not set");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_PATH + STATE_PATH + "?state=" + state).openConnection();

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
            case 403:
                throw new Exception("403 Not Authorized");
        }
    }

    public static JSONObject updateLocation(String latitude, String longitude) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_PATH + LOCATION_PATH).openConnection();
        connection.setDoOutput(true);
        String request = new JSONStringer()
                .object()
                .key("lat").value(latitude)
                .key("long").value(longitude)
                .endObject()
                .toString();
        connection.getOutputStream().write(request.getBytes());
        connection.getOutputStream().flush();
        connection.getOutputStream().close();

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
            case 403:
                throw new Exception("403 Not Authorized");
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while (true) {
            String temp = reader.readLine();
            if (temp == null) {
                break;
            }
            builder.append(temp);
        }
        JSONObject response = new JSONObject(builder.toString());
        return response;
    }

    public static JSONObject getConfig() throws Exception{
        HttpURLConnection connection = (HttpURLConnection) new URL(SERVER_PATH + CONFIG_PATH).openConnection();

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
        }
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        while (true) {
            String temp = reader.readLine();
            if (temp == null) {
                break;
            }
            builder.append(temp);
        }
        JSONObject response = new JSONObject(builder.toString());
        return response;
    }
}
