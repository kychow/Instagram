package comkychow.github.parsestagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // staging debug statement
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // monitors Parse OkHttp traffic: can be Level.BASIC, HEADERS, or BODY
        // http://square.github.io/okhttp/3.x/logging-interceptor/
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        // set applicationId, server based on Heroku settings values
        // clientKey only needed if explicity configured
        // network interceptors must be added with Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("kychow")
                .clientKey(null)
                .clientBuilder(builder)
                .server("http://kychow-fbu-instagram.herokuapp.com/parse").build());

        // test object creation
        ParseObject testObject = new ParseObject("testObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

    }
/*
    // creates new user in Parse app, checks for both unique username and email
    public void createNewUser() {
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername("joestevens");
        user.setPassword("secret123");
        user.setEmail("email@example.com");
        // Set custom properties
        user.put("phone", "650-253-0000");
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    // Hooray! Let them use the app now.
                } else {
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                    Log.e("Signup ParseException: ", e);
                }
            }
        });

    }
    */
}
