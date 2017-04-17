package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Baso on 12/3/2016.
 */
public class SearchResults {
    int code;
    @SerializedName("people")
    @Expose
    ArrayList<Subscribe> people;
    @SerializedName("location")
    @Expose
    ArrayList<Location> locations;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public ArrayList<Subscribe> getPeople() {
        return people;
    }

    public void setPeople(ArrayList<Subscribe> people) {
        this.people = people;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }
}
