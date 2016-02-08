package com.jaragua.avlmobile.utils;

public class Geo {

	public static long calculateDistance(double latA, double lonA, double latB, double lonB) {
		double distanceMeters;
		double latitude_A = (latA * Math.PI) / 180;
		double longitude_A = (lonA * Math.PI) / 180;
		double latitude_B = (latB * Math.PI) / 180;
		double longitude_B = (lonB * Math.PI) / 180;
		distanceMeters = (6371 * Math.acos(Math.cos(latitude_A) *
				Math.cos(latitude_B) * Math.cos(longitude_B - longitude_A) +
				Math.sin(latitude_A) * Math.sin(latitude_B))) * 1000;
		return Math.round(distanceMeters);
	}

}
