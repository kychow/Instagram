package comkychow.github.parsestagram.model;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

// name "Post" should be exact same as on parse dashboard
@ParseClassName("Post")
public class Post extends ParseObject {

    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_IMAGE       = "image";
    private static final String KEY_USER        = "user";

    public ParseFile getMedia() { return getParseFile("media"); }
    public void setMedia(ParseFile parseFile) { put("media", parseFile); }

    public String getDescription() {
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description) {
        put(KEY_DESCRIPTION, description);
    }

    public ParseFile getImage() {
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile image) {
        put(KEY_IMAGE, image);
    }

    // set as ParseUser instead of pointer
    // ensure query includes user in post requests
    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    // queries post models
    // context: post class -> inner static class
    public static class Query extends ParseQuery<Post> {
        public Query() {
            super(Post.class);
        }

        public Query getTop() {
           setLimit(20);
           return this; // builder pattern lets users to chain methods
        }

        public Query withUser() {
           include("user");
           return this;
        }
    }
}
