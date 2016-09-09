package uk.co.paulcowie.cinnotify.networking.services;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import java.util.ArrayList;
import java.util.List;

import uk.co.paulcowie.cinnotify.settings.adapters.ServiceDiscoveryAdapter;

/**
 * Created by paul on 08/09/16.
 */
public class ServiceDiscovery {
    private static final String TAG = ServiceDiscovery.class.getName();

    private NsdManager.DiscoveryListener discoveryListener;
    private NsdManager.ResolveListener resolveListener;
    private NsdManager nsdManager;
    private List<NsdServiceInfo> serviceInfos;

    private Context context;
    private BaseAdapter adapter;

    private static String CINNOTIFY_SERVICE_TYPE = "_cinnotify._tcp.";
    private static String CINNOTIFY_SERVICE_NAME = "Cinnotify";

    private boolean started = false;
    private Activity activity;

    public ServiceDiscovery(Activity activity, BaseAdapter adapter){
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.adapter = adapter;
        serviceInfos = new ArrayList<>();
    }

    public ServiceDiscovery(Activity activity){
        this(activity, null);
    }

    /**
     * Extracts the hostname from a {@link NsdServiceInfo} object.
     * @return The hostname, or the empty String if the service info was malformed.
     */
    @NonNull
    public static String getHostname(NsdServiceInfo serviceInfo){
        /* All Cinnotify servers prefix the service name with the hostname of the machine.
         * Since there doesn't seem to be any other way to get the hostname, this extracts it from
         * that service name.
         * The service name format is [hostname]-Cinnotify
         */

        String serviceName = serviceInfo.getServiceName();
        int lastDash = serviceName.lastIndexOf('-');

        if(lastDash == -1){
            return "";
        }

        return serviceName.substring(0, lastDash);
    }

    private void initListener(){
        nsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);

        resolveListener = new NsdManager.ResolveListener(){

            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                Log.e(TAG, "Resolve failed: " + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Resolve succeeded: " + serviceInfo);
                if(!serviceInfos.contains(serviceInfo)){
                    serviceInfos.add(serviceInfo);
                    if(adapter != null){
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }
                }
            }
        };

        discoveryListener = new NsdManager.DiscoveryListener(){

            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                nsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(TAG, "Service discovery started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.d(TAG, "Service discovery stopped: " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service discovery success " + serviceInfo);

                if(!serviceInfo.getServiceType().equals(CINNOTIFY_SERVICE_TYPE)){
                    Log.d(TAG, "Unknown service type: " + serviceInfo.getServiceType());
                }
                else if(serviceInfo.getServiceName().contains(CINNOTIFY_SERVICE_NAME)){
                    Log.d(TAG, "Got a Cinnotify server");

                    nsdManager.resolveService(serviceInfo, resolveListener);
                }

            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                Log.d(TAG, "Service lost " + serviceInfo);


            }


        };
    }

    public void setAdapter(BaseAdapter adapter){
        this.adapter = adapter;
    }

    public void start(){
        if(started){
            return;
        }

        started = true;
        initListener();
        nsdManager.discoverServices(CINNOTIFY_SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }

    public void stop(){
        if(!started){
            return;
        }

        started = false;
        nsdManager.stopServiceDiscovery(discoveryListener);
    }

    public void refresh(){
        if(started){
            stop();
        }
        serviceInfos.clear();
        start();
    }

    public List<NsdServiceInfo> getServiceInfos(){
        return serviceInfos;
    }

}
