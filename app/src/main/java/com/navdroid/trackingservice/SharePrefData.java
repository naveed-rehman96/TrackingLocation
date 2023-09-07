package com.navdroid.trackingservice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class SharePrefData {
    private final String LEVEL = "level";
    private final String STATUS = "isWorking";
    private final String LOV_STATUS = "lov_status";
    private final String TRACKING_ID = "tracking_id";
    private final String USER_PPNO = "userPPNO";
    private final String USER_NAME = "userName";
    private final String ENCRYPTION_CHECK = "encryption_check";
    private final String IMAGE_DATA = "imagedata";
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    public SharePrefData(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }


    public void setContext(Context context) {
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public Boolean getTrackingStatus() {
        return sp.getBoolean(STATUS, false);
    }

    public void setTrackingStatus(Boolean status) {
        try {
            spEditor = sp.edit();
            spEditor.putBoolean(STATUS, status);
            spEditor.apply();
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Boolean getIsLovDownloadCompletedStatus() {
        return sp.getBoolean(LOV_STATUS, false);
    }

    public void setIsLovDownloadCompletedStatus(Boolean status) {
        try {
            spEditor = sp.edit();
            spEditor.putBoolean(LOV_STATUS, status);
            spEditor.apply();
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Long getStartTrackingId() {
        return sp.getLong(TRACKING_ID, 0);
    }

    public void setStartTrackingId(Long id) {
        try {
            spEditor = sp.edit();
            spEditor.putLong(TRACKING_ID, id);
            spEditor.apply();
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUserPPNO(String ppno) {
        try {
            spEditor = sp.edit();
            spEditor.putString(USER_PPNO, ppno);
            spEditor.apply();
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserPPNo() {
        return sp.getString(USER_PPNO, "");
    }

    public void setUserNAME(String ppno) {
        try {
            spEditor = sp.edit();
            spEditor.putString(USER_NAME, ppno);
            spEditor.apply();
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getUserName() {
        return sp.getString(USER_NAME, "");
    }

    public Boolean getEncryptionCheck() {
        return sp.getBoolean(ENCRYPTION_CHECK, true);
    }

    public void setEncryptionCheck(Boolean isEncOn) {
        try {
            spEditor = sp.edit();
            spEditor.putBoolean(ENCRYPTION_CHECK, isEncOn);
            spEditor.apply();
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    /*@SuppressLint("CommitPrefEdits")
    public boolean destroyUserSession() {
        this.spEditor = this.sp.edit();
        this.spEditor.remove(USER_MSISDN);
        this.spEditor.remove(NAME);
        this.spEditor.remove(AMMOUNT);
        this.spEditor.remove(EMAIL);
        this.spEditor.apply();
        return true;
    }*/


}
