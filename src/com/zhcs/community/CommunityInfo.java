package com.zhcs.community;

public class CommunityInfo {
	private static String id;
	private static String address;
	private static String userName;
	private static Number latitude;
	private static Number longitude;
	public static String getId() {
		return id;
	}
	public static void setId(String id) {
		CommunityInfo.id = id;
	}
	public static String getAddress() {
		return address;
	}
	public static void setAddress(String address) {
		CommunityInfo.address = address;
	}
	public static String getUserName() {
		return userName;
	}
	public static void setUserName(String userName) {
		CommunityInfo.userName = userName;
	}
	public static Number getLatitude() {
		return latitude;
	}
	public static void setLatitude(Number latitude) {
		CommunityInfo.latitude = latitude;
	}
	public static Number getLongitude() {
		return longitude;
	}
	public static void setLongitude(Number longitude) {
		CommunityInfo.longitude = longitude;
	}
	
}
