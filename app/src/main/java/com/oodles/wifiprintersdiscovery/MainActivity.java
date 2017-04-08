package com.oodles.wifiprintersdiscovery;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Logger;

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class MainActivity extends AppCompatActivity implements View.OnClickListener, NsdManager.DiscoveryListener{

    private TextView discover;
    private Spinner printersSpinner;
    private  NsdManager nsdManager = null;
    private static final String SERVICE_TYPE = "_ipp._tcp";
    private ArrayList<String> list = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setEventListener();
        addItemOnSpinner(printersSpinner);
    }

    private void setEventListener() {
        discover.setOnClickListener(this);
    }

    private void initView() {
        discover = (TextView) findViewById(R.id.discover);
        printersSpinner = (Spinner) findViewById(R.id.printer_list);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void startPrintersDiscovery() {
        if(nsdManager == null){
            nsdManager = (NsdManager) getSystemService(Context.NSD_SERVICE);
            nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, this);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    nsdManager.stopServiceDiscovery(MainActivity.this);
                    nsdManager = null;
                }
            },15000);
        }
    }

    public void addItemOnSpinner(Spinner spinnerLabel){
        if(!list.contains("Select"))
            list.add("Select");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLabel.setAdapter(dataAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.discover:
                startPrintersDiscovery();
                break;
        }
    }

    @Override
    public void onStartDiscoveryFailed(String serviceType, int errorCode) {
        Log.d("started", "Service Discovery start Failed");
    }

    @Override
    public void onStopDiscoveryFailed(String serviceType, int errorCode) {
        Log.d("stopped", "Service Discovery Stopped Failed");
    }

    @Override
    public void onDiscoveryStarted(String serviceType) {
        Log.d("started", "Service Discovery Started");
    }

    @Override
    public void onDiscoveryStopped(String serviceType) {
        Log.d("stopped", "Service Discovery Stopped");
    }

    @Override
    public void onServiceFound(NsdServiceInfo serviceInfo) {
        Toast.makeText(this, "Service Discovery Found", Toast.LENGTH_SHORT).show();
        Log.d("SERVICETYPE", serviceInfo.getServiceType());
        Log.d("hostname", serviceInfo.getServiceName());
        if(!list.contains(serviceInfo.getServiceName())){
            list.add(serviceInfo.getServiceName());
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                addItemOnSpinner(printersSpinner);
                addItemOnSpinner(printersSpinner);
                addItemOnSpinner(printersSpinner);
            }
        });
        // nsdManager.resolveService(serviceInfo, this);
        nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.d("Failed", "Resolve failed");
            }
            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d("Resolved", "Service resolved");
                Log.d("Service Type", serviceInfo.getServiceType());
                if(String.valueOf(serviceInfo.getPort()) != null){
                    Log.d("Port", String.valueOf(serviceInfo.getPort()));
                }
                Log.d("Service Name", serviceInfo.getServiceName());
            }
        });
    }

    @Override
    public void onServiceLost(NsdServiceInfo serviceInfo) {

    }
}
