package ttit.com.shuvo.docdiary.report_manager.report_view.reports.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

public class ByteArrayRequest extends Request<byte[]> {

    private final Response.Listener<byte[]> listener;

    public ByteArrayRequest(int method, String url,
                            Response.Listener<byte[]> listener,
                            Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected Response<byte[]> parseNetworkResponse(NetworkResponse response) {
        return Response.success(response.data, HttpHeaderParser.parseCacheHeaders(response));
    }

    @Override
    protected void deliverResponse(byte[] response) {
        listener.onResponse(response);
    }
}