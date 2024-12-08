package com.example.lostandfoundpettracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PetReport implements Parcelable {
    private String id;
    private String status;
    private String petName;
    private String location;
    private long timestamp;
    private String description;
    private String petType;
    private String color;
    private double latitude;
    private double longitude;
    private String imageUrl;
    private String ownerId;
    private boolean isResolved;

    public PetReport() {
        // Default constructor required for calls to DataSnapshot.getValue(PetReport.class)
    }

    public PetReport(String id, String status, String petName, String location, long timestamp,
                     String description, String petType, String color, double latitude,
                     double longitude, String imageUrl, String ownerId) {
        this.id = id;
        this.status = status;
        this.petName = petName;
        this.location = location;
        this.timestamp = timestamp;
        this.description = description;
        this.petType = petType;
        this.color = color;
        this.latitude = latitude;
        this.longitude = longitude;
        this.imageUrl = imageUrl;
        this.ownerId = ownerId;
        this.isResolved = false;
    }

    // Getters and setters for all fields

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPetType() { return petType; }
    public void setPetType(String petType) { this.petType = petType; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public double getLatitude() { return latitude; }
    public void setLatitude(double latitude) { this.latitude = latitude; }

    public double getLongitude() { return longitude; }
    public void setLongitude(double longitude) { this.longitude = longitude; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public boolean isResolved() { return isResolved; }
    public void setResolved(boolean resolved) { isResolved = resolved; }

    public String getFormattedTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    // Parcelable implementation
    protected PetReport(Parcel in) {
        id = in.readString();
        status = in.readString();
        petName = in.readString();
        location = in.readString();
        timestamp = in.readLong();
        description = in.readString();
        petType = in.readString();
        color = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
        imageUrl = in.readString();
        ownerId = in.readString();
        isResolved = in.readByte() != 0;
    }

    public static final Creator<PetReport> CREATOR = new Creator<PetReport>() {
        @Override
        public PetReport createFromParcel(Parcel in) {
            return new PetReport(in);
        }

        @Override
        public PetReport[] newArray(int size) {
            return new PetReport[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(status);
        dest.writeString(petName);
        dest.writeString(location);
        dest.writeLong(timestamp);
        dest.writeString(description);
        dest.writeString(petType);
        dest.writeString(color);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(imageUrl);
        dest.writeString(ownerId);
        dest.writeByte((byte) (isResolved ? 1 : 0));
    }
}

