package models;

import android.graphics.Bitmap;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Baso on 10/21/2016.
 */
public class Post extends RealmObject{
    @SerializedName("id")
    @Expose
    @PrimaryKey
    private int idPost;
    @SerializedName("thumbnail")
    @Expose
    private String urlThumbnail;
    @SerializedName("foto")
    @Expose
    private String urlGambar;
    @SerializedName("locationName")
    @Expose
    private String locationName;
    @SerializedName("id_location")
    @Expose
    private int locationID;
    @SerializedName("jumlahLike")
    @Expose
    private int jumlahLike;
    @SerializedName("id_user")
    @Expose
    private int idUser;
    @SerializedName("nama")
    @Expose
    private String namaUser;
    @SerializedName("rating")
    @Expose
    private float rating;
    @SerializedName("review")
    @Expose
    private String review;
    @SerializedName("waktu")
    @Expose
    private String waktu;
    @SerializedName("foto_user")
    @Expose
    private String foto_user;
    private String statusTrending;
    private String statusNearby;
    public Post() {
    }


    public Post(int idPost, String urlThumbnail, String locationName) {
        this.idPost = idPost;

        this.urlThumbnail = urlThumbnail;
        this.locationName=locationName;
    }

    public Post(int idPost, String urlThumbnail, String urlGambar, int jumlahLike, int idUser, String locationName, int locationID, float rating, String review,String waktu) {
        this.idPost = idPost;
        this.urlThumbnail = urlThumbnail;
        this.urlGambar = urlGambar;
        this.jumlahLike = jumlahLike;
        this.idUser = idUser;
        this.rating = rating;
        this.review = review;
        this.locationName=locationName;
        this.locationID=locationID;
        this.waktu=waktu;
    }

    public int getIdPost() {
        return idPost;
    }

    public void setIdPost(int idPost) {
        this.idPost = idPost;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    public String getUrlGambar() {
        return urlGambar;
    }

    public void setUrlGambar(String urlGambar) {
        this.urlGambar = urlGambar;
    }

    public int getJumlahLike() {
        return jumlahLike;
    }

    public void setJumlahLike(int jumlahLike) {
        this.jumlahLike = jumlahLike;
    }

    public int getIdUser() {
        return idUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }
    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    public String getFoto_user() {
        return foto_user;
    }

    public void setFoto_user(String foto_user) {
        this.foto_user = foto_user;
    }

    public String getStatusTrending() {
        return statusTrending;
    }

    public void setStatusTrending(String statusTrending) {
        this.statusTrending = statusTrending;
    }

    public String getStatusNearby() {
        return statusNearby;
    }

    public void setStatusNearby(String statusNearby) {
        this.statusNearby = statusNearby;
    }
}
