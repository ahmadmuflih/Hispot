package models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;
/**
 * Created by Baso on 9/29/2016.
 */
public class Marker {
    @SerializedName("latitude")
    private float latitude;
    @SerializedName("longitude")
    private float longitude;
    private LatLng location;
    @SerializedName("type")
    private String type;
    @SerializedName("locationId")
    private String locationId;
    @SerializedName("locationName")
    private String locationName;
    @SerializedName("title")
    private String title;
    @SerializedName("thumbnail")
    private String thumbnail;

    public Marker() {
    }

    public Marker(float longitude, float latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Marker(float latitude, float longitude, String locationId) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.locationId = locationId;
    }

    public Marker(float latitude, float longitude, String type, String locationId, String locationName, String title, String thumbnail) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.locationId = locationId;
        this.locationName = locationName;
        this.title = title;
        this.thumbnail = thumbnail;

    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocationId() {
        return locationId;
    }

    public void setLocationId(String locationId) {
        this.locationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public LatLng getLocation(){
        return new LatLng(this.getLatitude(), this.getLongitude());
    }
    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
