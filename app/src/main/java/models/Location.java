package models;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Baso on 11/1/2016.
 */
public class Location {
    @SerializedName("locationId")
    @Expose
    private int LocationId;
    @SerializedName("locationName")
    @Expose
    private String locationName;
    @SerializedName("locationDistance")
    @Expose
    private String locationDistance;
    @SerializedName("latitude")
    @Expose
    private float latitude;
    @SerializedName("longitude")
    @Expose
    private float longitude;
    @SerializedName("locationPoint")
    @Expose
    private LatLng locationPoint;
    @SerializedName("vr")
    @Expose
    private String vr;
    public Location() {
    }

    public int getLocationId() {
        return LocationId;
    }

    public void setLocationId(int locationId) {
        LocationId = locationId;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }


    public String getLocationDistance() {
        return locationDistance;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public void setLocationDistance(String locationDistance) {
        this.locationDistance = locationDistance;
    }

    public LatLng getLocationPoint() {
        return locationPoint;
    }

    public void setLocationPoint(LatLng locationPoint) {
        this.locationPoint = locationPoint;
    }

    public String getVr() {
        return vr;
    }

    public void setVr(String vr) {
        this.vr = vr;
    }

    public Location(int locationId, String locationName, String locationDistance, LatLng locationPoint) {
        LocationId = locationId;
        this.locationName = locationName;
        this.locationDistance = locationDistance;
        this.locationPoint = locationPoint;
    }
}
