package id.edutech.baso.mapsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.Post;

/**
 * Created by Baso on 10/20/2016.
 */
public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.Holder> {
    private Context mContext;
    private ArrayList<Post> posts;
    private OnPostSelectedListener onPostSelectedListener;
    private final int TYPE_NEARBY=1;
    private final int TYPE_TRENDING=2;
    private final int TYPE_PROFILE=3;
    private final int TYPE_GALLERY=4;
    private int type;

    public ImageAdapter(Context mContext, ArrayList<Post> posts, int type) {
        this.mContext = mContext;
        this.posts = posts;
        this.type=type;
    }

    public void setOnPostSelectedListener(OnPostSelectedListener onPostSelectedListener) {
        this.onPostSelectedListener = onPostSelectedListener;
    }

    public void setPosts(ArrayList<Post> posts){
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.grid_item_layout, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Post post = posts.get(position);
        String simpanFoto ="thumbnail"+post.getIdPost()+".png";
        Bitmap foto = ImageUtil.getImage(simpanFoto, mContext);
        if (foto == null) {
            //holder.progressBar.setVisibility(View.VISIBLE);
            ImageUtil.capture(post.getUrlThumbnail(), simpanFoto, holder.imageView, new ImageUtil.ImageUtilListener() {
                @Override
                public void onDisplayed() {
                    holder.progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            holder.imageView.setImageBitmap(foto);
        }
        if(type!=TYPE_GALLERY)
            holder.textView.setText(post.getLocationName());
        else {
            holder.layout.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPostSelectedListener != null)
                    onPostSelectedListener.onSelected(post);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return posts.get(position).getIdPost();
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class Holder extends RecyclerView.ViewHolder {
        View itemView;
        ImageView imageView;
        ProgressBar progressBar;
        TextView textView;
        LinearLayout layout;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            imageView = (ImageView) itemView.findViewById(R.id.grid_item_image);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_post);
            textView = (TextView) itemView.findViewById(R.id.location_name);
            layout = (LinearLayout)itemView.findViewById(R.id.nama_layout);
        }
    }
    public interface OnPostSelectedListener{
        public void onSelected(Post post);
    }
}
