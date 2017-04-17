package id.edutech.baso.mapsproject;

import android.graphics.Bitmap;

/**
 * Created by Baso on 11/8/2016.
 */
public class MarkerIcon {
    String typeId;
    String typeName;
    boolean checked;
    Bitmap icon;
    public MarkerIcon(String typeId, String typeName, boolean checked, Bitmap icon) {
        this.typeId = typeId;
        this.typeName = typeName;
        this.checked = checked;
        this.icon = icon;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Bitmap getIcon() {
        return icon;
    }

    public void setIcon(Bitmap icon) {
        this.icon = icon;
    }
}
