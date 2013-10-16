package org.motechproject.ananya.kilkari.obd.repository;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

public class OBDHttpClient extends DefaultHttpClient {
    public OBDHttpClient(ClientConnectionManager connManager, int readTimeout, int connectionTimeout) {
        super(connManager);
        HttpParams params = getParams();
        HttpConnectionParams.setSoTimeout(params, readTimeout);
        HttpConnectionParams.setConnectionTimeout(params, connectionTimeout);
    }
}
