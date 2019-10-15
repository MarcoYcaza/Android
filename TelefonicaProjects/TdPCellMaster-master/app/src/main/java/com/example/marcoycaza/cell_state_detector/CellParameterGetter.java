package com.example.marcoycaza.cell_state_detector;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.telephony.CellIdentityGsm;
import android.telephony.CellIdentityLte;
import android.telephony.CellIdentityWcdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import javax.security.auth.login.LoginException;


public class CellParameterGetter {

    private TelephonyManager telephonyManager;
    private Application application;

    public CellParameterGetter(Application application) {
        this.application = application;
        this.telephonyManager = (TelephonyManager) application.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
    }

    @SuppressLint("MissingPermission")
    public CellRegistered action_monitor() {

        final CellRegistered cReg = new CellRegistered();

        try {
            cReg.setType(getNetworkClass(telephonyManager.getNetworkType()));

                switch (cReg.getType()){
                    case "GSM":

                        CellInfoGsm cellInfoGsm =
                                (CellInfoGsm) telephonyManager.getAllCellInfo().get(0);
                        CellIdentityGsm identityGsm = cellInfoGsm.getCellIdentity();
                        cReg.setDbm(cellInfoGsm.getCellSignalStrength().getDbm());
                        cReg.setCid(identityGsm.getCid());
                        cReg.setLac(identityGsm.getLac());
                        break;

                    case "WCDMA":

                        CellInfoWcdma cellinfoWcdma =
                                (CellInfoWcdma) telephonyManager.getAllCellInfo().get(0);
                        CellIdentityWcdma identityWcdma = cellinfoWcdma.getCellIdentity();
                        cReg.setDbm(cellinfoWcdma.getCellSignalStrength().getDbm());
                        cReg.setCid(identityWcdma.getCid()&0xffff);
                        cReg.setLac(identityWcdma.getLac());
                        cReg.setPsc(identityWcdma.getPsc());
                        break;

                    case "LTE":

                        CellInfoLte cellInfoLte =
                                (CellInfoLte) telephonyManager.getAllCellInfo().get(0);
                        CellIdentityLte identityLte = cellInfoLte.getCellIdentity();

                        cReg.setDbm(cellInfoLte.getCellSignalStrength().getDbm());
                        cReg.setCid(identityLte.getCi()/256);
                        cReg.setLac(identityLte.getTac());
                        cReg.setPci(identityLte.getPci());
                        break;
                    case "UNKNOWN":

                        cReg.setDbm(0);
                        cReg.setCid(0);
                        cReg.setLac(0);
                        cReg.setPci(0);

                        break;

                    default:
                        return null;
                }

                return cReg;

            } catch (Exception e) {
                        Log.i("CPG",e.toString());
        }

        return cReg;
    }

    private String getNetworkClass(int networkType) {

        switch (networkType) {

            case TelephonyManager.NETWORK_TYPE_GSM:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_GPRS:
                return "GSM";
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_HSPAP: // API level 13
                return "WCDMA";
            /*
             * Above API level 7, make sure to set android:targetSdkVersion
             * to appropriate level to use these
             */
            case TelephonyManager.NETWORK_TYPE_EHRPD: // API level 11
            case TelephonyManager.NETWORK_TYPE_EVDO_B: // API level 9

            case TelephonyManager.NETWORK_TYPE_IDEN: // API level 8
            case TelephonyManager.NETWORK_TYPE_LTE: // API level 11
                return "LTE";
            // Unknown
            case TelephonyManager.NETWORK_TYPE_UNKNOWN:
            default:
                return "UNKNOWN";
        }

    }

}
