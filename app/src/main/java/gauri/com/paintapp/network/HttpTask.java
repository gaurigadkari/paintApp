package gauri.com.paintapp.network;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Gauri Gadkari on 1/7/18.
 */

class HttpTask extends AsyncTask<HttpRequest, String, HttpResponse> {
    private static final String TAG = "HttpTask";
    private static final int TIMEOUT = 10000;

    @Override
    protected HttpResponse doInBackground(HttpRequest... params) {
        URL url;
        HttpURLConnection urlConnection = null;
        HttpResponse response = new HttpResponse();

        try {
            HttpRequest request = params[0];
            if (request == null || request.getURL() == null || request.getMethod() == null) {
                Log.e(TAG, "BAD HttpRequest");
                throw new Exception();
            }
            url = new URL(request.getURL());
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.v(TAG, request.getMethodString());
            urlConnection.setRequestMethod(request.getMethodString());
            if (request.getHeaders() != null) {
                for (HashMap.Entry<String, String> pair : request.getHeaders().entrySet()) {
                    urlConnection.setRequestProperty(pair.getKey(), pair.getValue());
                }
            }
            urlConnection.setConnectTimeout(TIMEOUT);
            urlConnection.setReadTimeout(TIMEOUT);
            if (request.getPostData() != null) {
                urlConnection.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(urlConnection.getOutputStream());
                byte[] postData = request.getPostData().getBytes();
                wr.write(postData);
            }
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            String responseString;
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                responseString = readStream(urlConnection.getInputStream());
            } else {
                responseString = readStream(urlConnection.getErrorStream());
            }
            Log.v(TAG, "Response code:" + responseCode);
            Log.v(TAG, responseString);
            response = new HttpResponse(responseCode, responseString, request.getCallback());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null)
                urlConnection.disconnect();
        }
        return response;
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    @Override
    protected void onPostExecute(HttpResponse response) {
        super.onPostExecute(response);
        response.getCallback().onResponse(response);
    }
}