package comkychow.github.parsestagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import comkychow.github.parsestagram.model.Post;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // register ParseApp as subclass
        ParseObject.registerSubclass(Post.class);
        // staging debug statement
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // monitors Parse OkHttp traffic: can be Level.BASIC, HEADERS, or BODY
        // http://square.github.io/okhttp/3.x/logging-interceptor/
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId & server based on Heroku settings
        // clientKey only needed if explicitly configured
        // network interceptors must be added with Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("kychow")
                .clientKey(null)
                .clientBuilder(builder)
                .server("http://kychow-fbu-instagram.herokuapp.com/parse").build());
    }
}
