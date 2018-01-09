package gauri.com.paintapp.network;

/**
 * Created by Gauri Gadkari on 1/7/18.
 */

public class HttpResponse {
    private int responseCode;
    private String response;
    private HttpRequest.RequestCallback callback;

    public HttpResponse(int responseCode, String response, HttpRequest.RequestCallback callback) {
        this.responseCode = responseCode;
        this.response = response;
        this.callback = callback;
    }

    public HttpResponse() {

    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public HttpRequest.RequestCallback getCallback() {
        return callback;
    }

    public void setCallback(HttpRequest.RequestCallback callback) {
        this.callback = callback;
    }
}
