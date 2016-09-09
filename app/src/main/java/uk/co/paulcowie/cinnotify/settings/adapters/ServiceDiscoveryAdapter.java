package uk.co.paulcowie.cinnotify.settings.adapters;

import android.net.nsd.NsdServiceInfo;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

import uk.co.paulcowie.cinnotify.networking.services.ServiceDiscovery;

/**
 * Created by paul on 09/09/16.
 */
public class ServiceDiscoveryAdapter extends BaseAdapter {

    private final List<NsdServiceInfo> serviceInfos;
    private final int resource;
    private final int hostnameResourceId;
    private final int ipResourceId;
    private final int portResourceId;


    public ServiceDiscoveryAdapter(@NonNull List<NsdServiceInfo> serviceInfos, int resource, int hostnameResourceId, int ipResourceId, int portResourceId) {
        this.serviceInfos = serviceInfos;
        this.resource = resource;
        this.hostnameResourceId = hostnameResourceId;
        this.ipResourceId = ipResourceId;
        this.portResourceId = portResourceId;
    }


    @Override
    public int getCount() {
        return serviceInfos.size();
    }

    @Override
    public NsdServiceInfo getItem(int position) {
        return serviceInfos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final View view;

        if(convertView == null){
            view = LayoutInflater.from(parent.getContext()).inflate(resource, parent, false);
        }
        else {
            view = convertView;
        }

        final TextView hostname;
        final TextView ip;
        final TextView port;

        hostname = (TextView) view.findViewById(hostnameResourceId);
        ip = (TextView) view.findViewById(ipResourceId);
        port = (TextView) view.findViewById(portResourceId);

        NsdServiceInfo item = getItem(position);

        hostname.setText(ServiceDiscovery.getHostname(item));
        ip.setText(String.format("IP: %s", item.getHost().getHostAddress()));
        port.setText(String.format("Port: %s", String.valueOf(item.getPort())));

        return view;
    }
}
