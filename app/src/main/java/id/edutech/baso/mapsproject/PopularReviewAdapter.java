package id.edutech.baso.mapsproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import id.edutech.baso.mapsproject.R;

import java.util.ArrayList;

import models.Post;

/**
 * Created by Baso on 10/23/2016.
 */
public class PopularReviewAdapter extends RecyclerView.Adapter<PopularReviewAdapter.Holder> {
    ArrayList <Post> listPost = new ArrayList<>();
    private Context mContext;
    private OnPostSelectedListener onPostSelectedListener;

    public PopularReviewAdapter(Context mContext,ArrayList<Post> listPost) {
        this.listPost = listPost;
        this.mContext = mContext;
    }

    public void setOnPostSelectedListener(OnPostSelectedListener onPostSelectedListener) {
        this.onPostSelectedListener = onPostSelectedListener;
    }
    @Override
    public Holder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.popular_cardlist, viewGroup, false);
        return new Holder(view);
    }
    @Override
    public void onBindViewHolder(final Holder holder, int position) {
        final Post post = listPost.get(position);
        holder.nama_review.setText(post.getNamaUser());
        holder.tgl_review.setText(post.getWaktu());
        holder.isi_review.setText("\""+post.getReview()+"\"");
        holder.rating_review.setRating(post.getRating());
          holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(onPostSelectedListener != null)
                    onPostSelectedListener.onSelected(post);
            }
        });
        Bitmap thumbnail = ImageUtil.getImage("thumbnail"+post.getIdPost()+".png", mContext);
        if(thumbnail==null) {
            ImageUtil.capture(post.getUrlThumbnail(), "thumbnail"+post.getIdPost()+".png", holder.foto_review, new ImageUtil.ImageUtilListener() {
                @Override
                public void onDisplayed() {
                    holder.progress_review.setVisibility(View.GONE);
                }
            });
        }
        else{
            holder.progress_review.setVisibility(View.GONE);
            holder.foto_review.setImageBitmap(thumbnail);
        }
    }
    @Override
    public long getItemId(int position) {
        return listPost.get(position).getIdPost();
    }
    @Override
    public int getItemCount() {
        return listPost.size();
    }


    public static class Holder extends RecyclerView.ViewHolder{
        View itemView;
        TextView nama_review;
        TextView tgl_review;
        TextView isi_review;
        RatingBar rating_review;
        ImageView foto_review;
        ProgressBar progress_review;
        public Holder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            nama_review = (TextView) itemView.findViewById(R.id.nama_review);
            tgl_review = (TextView) itemView.findViewById(R.id.tgl_review);
            isi_review = (TextView) itemView.findViewById(R.id.isi_review);
            rating_review = (RatingBar) itemView.findViewById(R.id.rating_review);
            foto_review = (ImageView) itemView.findViewById(R.id.foto_review);
            progress_review = (ProgressBar) itemView.findViewById(R.id.progress_review);
        }
    }
    public interface OnPostSelectedListener{
        void onSelected(Post post);
    }
}
