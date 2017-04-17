package models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Baso on 10/23/2016.
 */
public class Subscribe {
    @SerializedName("id")
    @Expose
    int idUser;
    @SerializedName("nama")
    @Expose
    String namaUser;
    @SerializedName("foto")
    @Expose
    String fotoUser;
    @SerializedName("background")
    @Expose
    String backgroundUser;
    @SerializedName("biodata")
    @Expose
    String keterangan;
    @SerializedName("status")
    @Expose
    boolean status;

    public Subscribe(int idUser, String namaUser, String fotoUser, String keterangan, boolean status) {
        this.idUser = idUser;
        this.namaUser = namaUser;
        this.fotoUser = fotoUser;
        this.keterangan = keterangan;
        this.status = status;
    }

    public Subscribe() {

    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    public String getNamaUser() {
        return namaUser;
    }

    public void setNamaUser(String namaUser) {
        this.namaUser = namaUser;
    }

    public String getFotoUser() {
        return fotoUser;
    }

    public void setFotoUser(String fotoUser) {
        this.fotoUser = fotoUser;
    }

    public String getBackgroundUser() {
        return backgroundUser;
    }

    public void setBackgroundUser(String backgroundUser) {
        this.backgroundUser = backgroundUser;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
