package id.edutech.baso.mapsproject;

/**
 * Created by Baso on 11/11/2016.
 */
public class Filter {
    String filterName;
    int filterCode;

    public Filter(String filterName, int filterCode) {
        this.filterName = filterName;
        this.filterCode = filterCode;
    }

    public String getFilterName() {
        return filterName;
    }

    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    public int getFilterCode() {
        return filterCode;
    }

    public void setFilterCode(int filterCode) {
        this.filterCode = filterCode;
    }
}
