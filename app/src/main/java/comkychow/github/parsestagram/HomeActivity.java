package comkychow.github.parsestagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import comkychow.github.parsestagram.model.Post;

/* @brief HomeActivity deals with the main logic and contains a menu with
* a recyclerView of posts, a new post and camera
 */
public class HomeActivity extends AppCompatActivity {

    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    PostAdapter postAdapter;
    ArrayList<Post> posts;
    RecyclerView rvPosts;
    File photoFile;

    @BindView(R.id.ivPreview) ImageView ivPreview;
    @BindView(R.id.camera_btn) Button cameraButton;
    @BindView(R.id.post_btn) Button postButton;
    @BindView(R.id.tvNewCaption) EditText tvNewCaption;
    @BindView(R.id.postToolbar) View postToolbar;
    private SwipeRefreshLayout swipeContainer;
    @BindView(R.id.bottom_navigation) BottomNavigationView bottomNavigationView;

    // custom action bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Logout logic
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_btn) {
            ParseUser.logOut();
            final Intent logoutIntent = new Intent(HomeActivity.this, MainActivity.class);
            startActivity(logoutIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        postToolbar.setVisibility(View.GONE);
        ivPreview.setVisibility(View.GONE);

        // TIMELINE FUNCTIONALITY
        rvPosts= (RecyclerView) findViewById(R.id.rvPost);
        // initialize arrayList (data source)
        posts = new ArrayList<>();
        // construct adapter from datasource
        postAdapter = new PostAdapter(posts);
        // RecyclerView setup (Layout Manager, user Adapter)
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        // set adapter
        rvPosts.setAdapter(postAdapter);

        // TIMELINE FUNCTIONALITY - similar to populateTimeline
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser().orderByDescending("createdAt");
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Post post = objects.get(i);
                        posts.add(post);
                        postAdapter.notifyItemInserted(posts.size() - 1);
                    }
                } else {
                    e.printStackTrace();
                }
            }
        });

        // NEW POST FUNCTIONALITY - display camera / post buttons
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                ivPreview.setVisibility(View.GONE);
                switch (item.getItemId()) {
                    default:
                    case R.id.home_btn:
                        rvPosts.setVisibility(View.VISIBLE);
                        postToolbar.setVisibility(View.GONE);
                        return true;
                    case R.id.new_post_btn:
                        rvPosts.setVisibility(View.GONE);
                        postToolbar.setVisibility(View.VISIBLE);
                        return true;
                    case R.id.user_btn:
                        rvPosts.setVisibility(View.GONE);
                        postToolbar.setVisibility(View.GONE);
                        return true;
                }
            }
        });

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync();
                swipeContainer.setRefreshing(false);
            }
        });

        // CAMERA FUNCTIONALITY
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //photoFile = getPhotoFileUri(photoFileName);
                photoFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
                Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(),"com.codepath.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);
                // check if null: app crashes if startActivityforResult has no app to handle it
                if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                }
            }
        });

        // POST FUNCTIONALITY
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String description = tvNewCaption.getText().toString();
                final ParseUser user = ParseUser.getCurrentUser();
                final ParseFile parseFile = new ParseFile(photoFile);
                createPost(description, parseFile, user);
            }
        });

        // DETAIL FUNCTIONALITY

    }
    private void createPost(String  description, ParseFile imageFile, ParseUser user) {
        final Post newPost = new Post();
        newPost.setDescription(description);
        newPost.setImage(imageFile);
        newPost.setUser(user);

        newPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Toast.makeText(HomeActivity.this, "Post successfully created.", Toast.LENGTH_SHORT).show();
                    posts.add(newPost);
                    postAdapter.notifyItemInserted(posts.size() - 1);
                    ivPreview.setVisibility(View.GONE);
                    Log.d("HomeActivity", "Create post success!");
                } else { e.printStackTrace(); }
            }
        });
    }

    // pass path to post activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // camera photo is on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ivPreview.setImageBitmap(takenImage);
                ivPreview.setVisibility(View.VISIBLE);
            } else { // Result was a failure
                Toast.makeText(this, "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void fetchTimelineAsync() {
        // Send the network request to fetch the updated data
        // `client` here is an instance of Android Async HTTP
        // getHomeTimeline is an example endpoint.
        postAdapter.clear();
        final Post.Query postsQuery = new Post.Query();
        postsQuery.getTop().withUser().orderByDescending("createdAt");
        postsQuery.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> objects, ParseException e) {
                if (e == null) {
                    for (int i = 0; i < objects.size(); i++) {
                        Post post = objects.get(i);
                        posts.add(post);
                        postAdapter.notifyItemInserted(posts.size() - 1);

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
}
