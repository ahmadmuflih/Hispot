package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Baso on 12/3/2016.
 */
public class ProfileResults {
    @SerializedName("jumlah_post")
    @Expose
    int jumlahPost;
    @SerializedName("jumlah_like")
    @Expose
    int jumlahLike;
    @SerializedName("subscribing")
    @Expose
    int subscribing;
    @SerializedName("subscribers")
    @Expose
    int subscribers;
    @SerializedName("posts")
    @Expose
    ArrayList<Post> posts;

    public int getJumlahPost() {
        return jumlahPost;
    }

    public void setJumlahPost(int jumlahPost) {
        this.jumlahPost = jumlahPost;
    }

    public int getJumlahLike() {
        return jumlahLike;
    }

    public void setJumlahLike(int jumlahLike) {
        this.jumlahLike = jumlahLike;
    }

    public int getSubscribing() {
        return subscribing;
    }

    public void setSubscribing(int subscribing) {
        this.subscribing = subscribing;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }
}
