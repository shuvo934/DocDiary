package ttit.com.shuvo.docdiary.utilities;

import android.content.Context;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public final class VolleySingleton {
    private static volatile RequestQueue queue;

    private VolleySingleton() {}

    public static RequestQueue getQueue(Context context) {
        if (queue == null) {
            synchronized (VolleySingleton.class) {
                if (queue == null) {
                    queue = Volley.newRequestQueue(context.getApplicationContext());
                }
            }
        }
        return queue;
    }
}