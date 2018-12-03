package pl.szymonhanzel.alarmeserver.models;

import com.google.cloud.Timestamp;

/**
 * Klasa POJO reprezentująca pojedyncze urządzenie, które może zostać powiadomione
 */
public class User {

    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private String token;

    public User() {
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
