package comkychow.github.parsestagram;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import comkychow.github.parsestagram.model.Post;

/* need an adapter to load individual posts into RecyclerView */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder> {

    private List<Post> mPosts;
    private Activity activity;
    Context context;

    // pass in tweets array in constructor
    public PostAdapter(List<Post>posts) {
        mPosts = posts;
        this.activity = activity;
    }

    // for each row, inflate layout and cache refs into ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_post, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);
        return viewHolder;
    }

    // bind values based on element position
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // get data from position
        Post post = mPosts.get(position);

        // populate views according to data
        holder.tvCaption.setText(post.getDescription());
        holder.tvUserName.setText(post.getUser().getUsername());
        holder.tvTimestamp.setText(post.getCreatedAt().toString());

        Glide.with(context).load(post.getImage().getUrl()).into(holder.ivPost);
        //Glide.with(context).load(post.getImage().getUrl()).into(holder.ivProfileImage);

    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    // create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.ivProfileImage) public ImageView ivProfileImage;
        @BindView(R.id.tvUserName) public TextView tvUserName;
        @BindView(R.id.ivPost) public ImageView ivPost;
        @BindView(R.id.tvCaption) public TextView tvCaption;
        @BindView(R.id.tvTimestamp) public TextView tvTimestamp;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // Clean all elements of the recycler
    public void clear() {
        mPosts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items -- change to type used
    public void addAll(List<Post> list) {
        mPosts.addAll(list);
        notifyDataSetChanged();
    }


}

