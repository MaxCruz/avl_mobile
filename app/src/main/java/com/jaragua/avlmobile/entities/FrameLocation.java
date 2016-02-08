package com.jaragua.avlmobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class FrameLocation implements Parcelable {

	private double latitude;
	private double longitude;
	private double altitude;
	private float speed;
	private float accuracy;
	private float bearing;
	private long distance;
	private Date gpsDate;

	public FrameLocation(double latitude, double longitude, double altitude, float speed,
						 float accurancy, Date gpsDate) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.altitude = altitude;
		this.speed = speed;
		this.accuracy = accurancy;
		this.gpsDate = gpsDate;
	}

	public FrameLocation(Parcel in) {
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.altitude = in.readDouble();
		this.speed = in.readFloat();
		this.accuracy = in.readFloat();
		this.bearing = in.readFloat();
		this.distance = in.readLong();
		this.gpsDate = (Date) in.readSerializable();
	}

	public boolean isValid() {
		return this.latitude != 0 && this.longitude != 0;
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

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = (speed / 1000) * 3600;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public float getBearing() {
		return bearing;
	}

	public void setBearing(float bearing) {
		this.bearing = bearing;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public Date getGpsDate() {
		return gpsDate;
	}

	public void setGpsDate(long gpsDate) {
		this.gpsDate = new Date(gpsDate);
	}

	@Override
	public String toString() {
		return "FrameLocation [latitude=" + latitude + ", longitude=" + longitude + ", altitude=" +
                altitude + ", speed=" + speed + ", accurancy=" + accuracy + ", gpsDate=" +
                gpsDate + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeDouble(this.latitude);
		out.writeDouble(this.longitude);
		out.writeDouble(this.altitude);
		out.writeFloat(this.speed);
		out.writeFloat(this.accuracy);
		out.writeFloat(this.bearing);
		out.writeLong(this.distance);
		out.writeSerializable(this.gpsDate);
	}

	public static final Creator<FrameLocation> CREATOR = new Creator<FrameLocation>() {

		public FrameLocation createFromParcel(Parcel in) {
			return new FrameLocation(in);
		}

		public FrameLocation[] newArray(int size) {
			return new FrameLocation[size];
		}

	};

}
