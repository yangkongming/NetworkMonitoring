package www.pdx.life.networkmonitoring.httpdebug.interceptor;

import java.util.HashSet;
import java.util.Set;

import okhttp3.logging.HttpLoggingInterceptor;

public class RequestsLogger implements HttpLoggingInterceptor.Logger {

    private Set<HttpLogListener> listeners = new HashSet<>();

    public void addListener(HttpLogListener listener) {
        listeners.add(listener);
    }

    public void removeListener(HttpLogListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void log(String message) {
        for(HttpLogListener listener : listeners){
            listener.onMessage(message);
        }
    }

    public interface HttpLogListener {
        void onMessage(String msg);
    }
}
