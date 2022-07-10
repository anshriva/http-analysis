package com.anubhav.client.apache.helpers;

import org.apache.hc.client5.http.HttpRoute;
import org.apache.hc.client5.http.impl.io.BasicHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.util.TimeValue;

public class ApacheConnectionManagerFactory {
    public static AsyncClientConnectionManager getAsyncClientManager(String hostName, int port ){
                var poolingConnectionManager = new PoolingAsyncClientConnectionManager();
                poolingConnectionManager.setMaxTotal(5);
                poolingConnectionManager.setDefaultMaxPerRoute(4);

                HttpHost host = new HttpHost(hostName, port);
                poolingConnectionManager.setMaxPerRoute(new HttpRoute(host), 5);
                return poolingConnectionManager;
    }

    public static HttpClientConnectionManager getClientConnectionManager(ApacheConnectionManagerType connectionManagerType,
                                                                         String hostName,
                                                                         int port){
        switch (connectionManagerType) {
            case Basic -> {
                var basic =  new BasicHttpClientConnectionManager();
                basic.setValidateAfterInactivity(TimeValue.ofSeconds(100));
                basic.setSocketConfig(SocketConfig.DEFAULT);
                return basic;
            }
            case Pooling -> {
                var poolingConnectionManager = new PoolingHttpClientConnectionManager();
                poolingConnectionManager.setMaxTotal(5);
                poolingConnectionManager.setDefaultMaxPerRoute(4);
                HttpHost host = new HttpHost(hostName, port);
                poolingConnectionManager.setMaxPerRoute(new HttpRoute(host), 5);
                return poolingConnectionManager;
            }
        }
            return new BasicHttpClientConnectionManager();
        }
    }


