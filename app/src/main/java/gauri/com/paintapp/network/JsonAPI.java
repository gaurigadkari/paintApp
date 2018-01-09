package gauri.com.paintapp.network;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Gauri Gadkari on 1/7/18.
 */

public class JsonAPI {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String APPLICATION_JSON = "application/json";
    private static final String ACCEPT = "Accept";

    public interface JsonCallback {
        void onResponse(int statusCode, JSONObject json);
    }

    private static JsonAPI ourInstance = new JsonAPI();
    private HashMap<String, String> headers = new HashMap<>();

    public static JsonAPI getInstance() {
        return ourInstance;
    }

    private JsonAPI() {
        headers.put(CONTENT_TYPE, APPLICATION_JSON);
        headers.put(ACCEPT, APPLICATION_JSON);
    }

    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    public void removeHeader(String name) {
        headers.remove(name);
    }

    public void get(String url, final JsonCallback callback) {
        HttpRequest request = new HttpRequest(HttpRequest.Method.GET, url);
        request.setHeaders(headers);
        request.setCallback(callbackForJsonCallback(callback));
        new HttpTask().execute(request);
    }

    public void post(String url, JSONObject postData, JsonCallback callback) {
        HttpRequest request = new HttpRequest(HttpRequest.Method.POST, url);
        request.setHeaders(headers);
        request.setCallback(callbackForJsonCallback(callback));
        request.setPostData(postData.toString());
        new HttpTask().execute(request);
    }

    private HttpRequest.RequestCallback callbackForJsonCallback(final JsonCallback jsonCallback) {
        return new HttpRequest.RequestCallback() {
            @Override
            public void onResponse(HttpResponse response) {
                JSONObject json = null;
                try {
                    json = new JSONObject(response.getResponse());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                jsonCallback.onResponse(response.getResponseCode(), json);
            }
        };
    }
}
