package loredan13.hearth_client;

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
    public static final String MOOD_PATH = "/api/mood";
    public static final String CONFIG_PATH = "/api/config";
    public static final String STATE_PATH = "/api/state";
    public static final String LOCATION_PATH = "/api/location";
    public static final String PROTOCOL = "http://";

    public static String SERVER_PATH;

    public static void setServer(String path) {
        SERVER_PATH = path;
    }

    public static String auth(String user, String password) throws Exception {
        if (SERVER_PATH == null) {
            throw new Exception("Server path not set");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(PROTOCOL + SERVER_PATH + ":" + PORT + AUTH_PATH)
                .openConnection();
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
        return response.getString("token");
    }

    public static void updateMood(String token, int mood) throws Exception {
        if (SERVER_PATH == null) {
            throw new Exception("Server path not set");
        }

        if (token == null) {
            throw new Exception("Token not set");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(PROTOCOL + SERVER_PATH + ":" + PORT + MOOD_PATH + "?mood=" + mood)
                .openConnection();
        connection.setRequestProperty("Token", token);

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
            case 403:
                throw new Exception("403 Not Authorized");
            default:
                throw new Exception(String.valueOf(connection.getResponseCode()));
        }
    }

    public static void updateState(String token, int state) throws Exception {
        if (SERVER_PATH == null) {
            throw new Exception("Server path not set");
        }

        if (token == null) {
            throw new Exception("Token not set");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(PROTOCOL + SERVER_PATH + ":" + PORT + STATE_PATH + "?state=" + state)
                .openConnection();
        connection.setRequestProperty("Token", token);

        switch (connection.getResponseCode()) {
            case 200:
                break;
            case 400:
                throw new Exception("400 Bad Request");
            case 403:
                throw new Exception("403 Not Authorized");
        }
    }

    public static JSONObject updateLocation(String token, String latitude, String longitude) throws Exception {
        if (SERVER_PATH == null) {
            throw new Exception("Server path not set");
        }

        if (token == null) {
            throw new Exception("Token not set");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(PROTOCOL + SERVER_PATH + ":" + PORT + LOCATION_PATH)
                .openConnection();
        connection.setRequestProperty("Token", token);
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

    public static JSONObject getConfig() throws Exception {
        if (SERVER_PATH == null) {
            throw new Exception("Server path not set");
        }

        HttpURLConnection connection = (HttpURLConnection) new URL(PROTOCOL + SERVER_PATH + ":" + PORT + CONFIG_PATH)
                .openConnection();

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
