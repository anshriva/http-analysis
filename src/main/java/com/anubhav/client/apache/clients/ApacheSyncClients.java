package com.anubhav.client.apache.clients;

import com.anubhav.Constants;
import com.anubhav.client.apache.helpers.ApacheConnectionManagerFactory;
import com.anubhav.client.apache.helpers.ApacheConnectionManagerType;
import org.apache.hc.client5.http.ConnectionKeepAliveStrategy;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpUriRequest;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.http.protocol.HttpContext;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.util.TimeValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import static com.anubhav.Constants.*;
import static org.apache.hc.core5.http.HttpVersion.HTTP;

public class ApacheSyncClients {
    private static Logger logger = LoggerFactory.getLogger(ApacheSyncClients.class);

    public static void main(String[] args) {
        run();
    }
    public static void run()  {

        List<ApacheConnectionManagerType> connectionManagerTypes = Arrays.asList(
                ApacheConnectionManagerType.Basic,
                ApacheConnectionManagerType.Pooling);

        for(ApacheConnectionManagerType apacheConnectionManagerType : connectionManagerTypes){
            HttpUriRequest uriRequest = new HttpGet(uri);
            CloseableHttpClient client =getHttpClient(apacheConnectionManagerType);
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            for(int x= 0;x< NumberOfLoops; x++){
                try {
                    CloseableHttpResponse response = client.execute(uriRequest);
                    logger.debug("got the response = {}", EntityUtils.toString(response.getEntity()));
                } catch (IOException e) {
                    logger.error("got error = {} for request number {}", e.getMessage(), x);
                    client =getHttpClient(apacheConnectionManagerType);
                    // throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }

            stopWatch.stop();
            logger.info("total time taken for sync {} = {}", apacheConnectionManagerType, stopWatch.getTotalTimeMillis());
            client.close(CloseMode.GRACEFUL);
            logger.info("*************************************");
        }
    }

    private static CloseableHttpClient getHttpClient(ApacheConnectionManagerType connectionManagerType){
        HttpClientConnectionManager clientConnectionManager = ApacheConnectionManagerFactory.
                getClientConnectionManager(connectionManagerType, baseDomain,
                port);

        CloseableHttpClient httpClient = HttpClients.custom().
                setConnectionManager(clientConnectionManager).
                setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
                    @Override
                    public TimeValue getKeepAliveDuration(HttpResponse httpResponse, HttpContext httpContext) {
                        return TimeValue.ofSeconds(10);
                    }
                }).
                build();
        return httpClient;
    }
}
