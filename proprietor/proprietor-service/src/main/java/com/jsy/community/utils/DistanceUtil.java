package com.jsy.community.utils;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.HashMap;
import java.util.Map;

public class DistanceUtil {

	private static GeodeticCalculator geodeticCalculator =  new GeodeticCalculator();
	
    public static void main(String[] args) {
        System.out.println("经纬度距离计算结果：" + getDistance(106.574038, 29.649656, 106.555435, 29.558904));
        System.out.println("经纬度距离计算结果：" + getDistance(106.574038, 29.649656, 106.584048, 29.647656));
    }

    //private static DecimalFormat formatter = new DecimalFormat("00.00");
    
    public static Map<String, Object> getDistance(double longitudeFrom, double latitudeFrom, double longitudeTo, double latitudeTo) {
    	Map<String, Object> map = new HashMap<>();
        GlobalCoordinates source = new GlobalCoordinates(latitudeFrom, longitudeFrom);
        GlobalCoordinates target = new GlobalCoordinates(latitudeTo, longitudeTo);
        double distance;
        try {
        	distance = geodeticCalculator.calculateGeodeticCurve(Ellipsoid.Sphere, source, target).getEllipsoidalDistance();
		} catch (Exception e) {
			return null;
		}
        map.put("distanceDouble", distance);
        if(distance<1000){
        	map.put("distanceString", String.format("%.0f",distance) + "m");
        	return map;
        }
        map.put("distanceString", String.format("%.1f",distance/1000) + "km");
        return map;
    }
    
}
