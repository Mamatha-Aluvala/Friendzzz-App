package com.sinch.android.rtc.sample.video;

import com.sinch.android.rtc.SinchError;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends BaseActivity implements SinchService.StartFailedListener {

    private Button mLoginButton;
    private EditText mLoginName;
    private ProgressDialog mSpinner;
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);


        t=(TextView)findViewById(R.id.textView);

        mLoginName = (EditText) findViewById(R.id.loginName);

        mLoginButton = (Button) findViewById(R.id.loginButton);
        mLoginButton.setEnabled(false);

        mLoginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                loginClicked();
            }
        });
    }



    @Override
    protected void onServiceConnected() {
        //NetworkInfo mobileInfo= ConnectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        //boolean mobileConnected=mobileInfo.getState()==NetworkInfo.State.CONNECTED;
        mLoginButton.setEnabled(true);
        getSinchServiceInterface().setStartListener(this);
    }

    @Override
    protected void onPause() {
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
        super.onPause();
    }

    @Override
    public void onStartFailed(SinchError error) {
        Toast.makeText(this, error.toString(), Toast.LENGTH_LONG).show();
        if (mSpinner != null) {
            mSpinner.dismiss();
        }
    }

    @Override
    public void onStarted() {
        openPlaceCallActivity();
    }

    private void loginClicked() {

            String s="";
        int a=-1;
            s=getNetworkClass(getApplicationContext());
      t.setText("Network Type: "+s);
            if(!s.equals("4G"))
            {

                Toast.makeText(this, "Please connect only through 4G network", Toast.LENGTH_LONG).show();
                return;
            }

        String userName = mLoginName.getText().toString();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter a name", Toast.LENGTH_LONG).show();
            return;
        }

        if (!getSinchServiceInterface().isStarted()) {
            getSinchServiceInterface().startClient(userName);
            showSpinner();
        } else {
            openPlaceCallActivity();
        }
    }

    public String getNetworkClass(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();

        ConnectivityManager cm=(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean iswifi=cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
        if (iswifi==true)
        {
            return "WIFI";
        }


        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2G";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                return "3G";
            case TelephonyManager.NETWORK_TYPE_LTE:

                return "4G";
            default:
                return "Unknown";
        }
       //return networkType;
    }

    private void openPlaceCallActivity() {
        Intent mainActivity = new Intent(this, PlaceCallActivity.class);
        startActivity(mainActivity);
    }

    private void showSpinner() {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Logging in");
        mSpinner.setMessage("Please wait...");
        mSpinner.show();
    }
}
