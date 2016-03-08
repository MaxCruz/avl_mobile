package com.jaragua.avlmobile.entities;

import android.os.Parcel;
import android.os.Parcelable;

@SuppressWarnings("unused")
public class FrameLocation implements Parcelable {

	private int motive;
	private String date;
	private double latitude = 0;
	private double longitude = 0;
	private double altitude;
	private float speed;
	private float course;
	private long distance;
	private float accuracy;
	private String imei;
	private String provider;

	public FrameLocation() {
	}

	public FrameLocation(Parcel in) {
		this.motive = in.readInt();
		this.date = in.readString();
		this.latitude = in.readDouble();
		this.longitude = in.readDouble();
		this.altitude = in.readDouble();
		this.speed = in.readFloat();
		this.course = in.readFloat();
		this.distance = in.readLong();
		this.accuracy = in.readFloat();
		this.imei = in.readString();
		this.provider = in.readString();
	}

	public boolean isValid() {
		return this.latitude != 0 && this.longitude != 0;
	}

	public int getMotive() {
		return motive;
	}

	public void setMotive(int motive) {
		this.motive = motive;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
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

	public float getCourse() {
		return course;
	}

	public void setCourse(float course) {
		this.course = course;
	}

	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}

	public float getAccuracy() {
		return accuracy;
	}

	public void setAccuracy(float accuracy) {
		this.accuracy = accuracy;
	}

	public String getImei() {
		return imei;
	}

	public void setImei(String imei) {
		this.imei = imei;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(this.motive);
		out.writeString(this.date);
		out.writeDouble(this.latitude);
		out.writeDouble(this.longitude);
		out.writeDouble(this.altitude);
		out.writeFloat(this.speed);
		out.writeFloat(this.course);
		out.writeLong(this.distance);
		out.writeFloat(this.accuracy);
		out.writeString(this.imei);
		out.writeString(this.provider);
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
