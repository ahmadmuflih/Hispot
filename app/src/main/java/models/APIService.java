package models;

import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

/**
 * Created by Baso on 11/12/2016.
 */
public interface APIService {

    @FormUrlEncoded
    @POST("login.php")
    Call<User> getLogin(@Field("email") String email,@Field("nama") String nama,@Field("foto") String foto);

    @FormUrlEncoded
    @POST("validate.php")
    Call<Validation> getValidation(@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getmarkers.php")
    Call<List<Marker>> getMarkerDetails(@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("addmarker.php")
    Call<Validation> addMarker(@Field("locationName") String locationName,@Field("locationType") String locationType,@Field("latitude") double latitude,@Field("longitude") double longitude,@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getlocationpost.php")
    Call<ArrayList<Post>> getLocationPost(@Field("c") String category, @Field("location_id") String location_id, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("search.php")
    Call<SearchResults> getSearch(@Field("query") String query, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("like.php")
    Call<Validation> getLike(@Field("a") String action,@Field("post_id") int post_id, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("report.php")
    Call<Validation> addReport(@Field("email") String email,@Field("reason") String reason,@Field("post_id") int post_id, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("update.php")
    Call<Validation> updateProfile(@Field("f") String function_name, @Field("nama") String name,@Field("bio") String bio, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getprofile.php")
    Call<ProfileResults> getProfile(@Field("user_id") String user_id,@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getsubscribe.php")
    Call<ArrayList<Subscribe>> getSubscribing(@Field("c") String category,@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getsubscribe.php")
    Call<ArrayList<Subscribe>> getSubscriber(@Field("c") String category,@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("subscribe.php")
    Call<Validation> subscribe(@Field("subscribed_id") String subscribed_id,@Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getvr.php")
    Call<Validation> getvr(@Field("location_id") String location_id,@Field("api_key") String api_key);



    @Multipart
    @POST("post.php")
    Call<Validation> uploadFile(@Part MultipartBody.Part file, @Part("file") RequestBody name,@Query("review") String review,@Query("rating") float rating,@Query("location_id") String location_id,@Query("api_key") String api_key);

    @FormUrlEncoded
    @POST("feedback.php")
    Call<Validation> addFeedback(@Field("nama") String nama,@Field("isi") String isi, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("getnearby.php")
    Call<PostResults> getNearby(@Field("latitude") String latitude,@Field("longitude") String longitude, @Field("api_key") String api_key);

    @FormUrlEncoded
    @POST("gettrending.php")
    Call<PostResults> getTrending(@Field("latitude") String latitude, @Field("longitude") String longitude, @Field("api_key") String api_key);

    public final String BASE_URL = "http://edutechteam.hol.es/";
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    APIService service = retrofit.create(APIService.class);

}
