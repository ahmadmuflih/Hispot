package models;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Baso on 9/30/2016.
 */
public interface MarkersAPI {

    @GET("api/json/get/ceIGgRigrS?indent=2")
    Call<List<Marker>> getMarkerDetails();
}
