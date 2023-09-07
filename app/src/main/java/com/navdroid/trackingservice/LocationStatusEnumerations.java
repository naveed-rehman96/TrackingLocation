package com.navdroid.trackingservice;

public enum LocationStatusEnumerations {

    START(1),
    STOP(2);

    public int code;

    LocationStatusEnumerations(int code) {
        this.code = code;
    }

}
