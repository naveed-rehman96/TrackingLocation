package com.navdroid.trackingservice;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


public class GPS {
    private final IGPSActivity main;
    private final LocationListener mlocListener;
    private final LocationManager mlocManager;
    private Boolean isFromService;

    private final int MINIMUM_TIME = 4000;
    private final int MINIMUM_RADIUS = 4;


    @SuppressLint("MissingPermission")
    public GPS(IGPSActivity main) {
        Log.e("mylocation","location enabled");

        this.main = main;
        mlocManager = (LocationManager) ((Activity) this.main).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        try{
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 4, mlocListener);
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 4, mlocListener);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public GPS(IGPSActivity main, Boolean isFromService) {
        Log.e("mylocation","location enabled from service");

        this.main = main;
        this.isFromService = isFromService;

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_HIGH);
        criteria.setAltitudeRequired(false);
        criteria.setSpeedRequired(true);
        criteria.setCostAllowed(true);
        criteria.setBearingRequired(false);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);

        mlocManager = (LocationManager) ((Service) this.main).getSystemService(Context.LOCATION_SERVICE);
        mlocListener = new MyLocationListener();
        //mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MINIMUM_TIME, MINIMUM_RADIUS, mlocListener);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME, MINIMUM_RADIUS, mlocListener);
        mlocManager.requestLocationUpdates(MINIMUM_TIME, MINIMUM_RADIUS, criteria, mlocListener, null);

    }

    public void stopGPS() {
        mlocManager.removeUpdates(mlocListener);
    }

    public Location getLastLocation(String value) {
        return mlocManager.getLastKnownLocation(value);
    }

    public void resumeGPS() {
        if (isFromService) {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 5, mlocListener);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 5, mlocListener);
        } else {
            mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mlocListener);
            mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mlocListener);
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
//            GPS.this.main.locationChanged(loc.getLongitude(), loc.getLatitude());
            Log.e("mylocation","i got location");
            GPS.this.main.locationChanged(loc);
        }

        @Override
        public void onProviderDisabled(String provider) {
//GPS.this.main.displayGPSSettingsDialog();
            Log.e("mylocation","provider disable");

        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.e("mylocation","provider enable");

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

    }

}