package pl.szymonhanzel.alarmeserver.models;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.GeoPoint;

public class Alarm {

    private String vehicleType;
    private Timestamp timestamp;
    private double latitude;
    private double longitude;
    private double altitude;

    /**
     * Klasa POJO reprezentująca alarm
     */
    public Alarm(){}


    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
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

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }
}
