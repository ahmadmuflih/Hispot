package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Baso on 12/3/2016.
 */
public class PostResults {
    @SerializedName("code")
    @Expose
    int code;
    @SerializedName("posts")
    @Expose
    ArrayList<Post> posts;
    @SerializedName("lokasi")
    @Expose
    Location location;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<Post> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<Post> posts) {
        this.posts = posts;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
