package com.anubhav.client.apache.clients;

import com.anubhav.Constants;
import com.anubhav.client.apache.helpers.ApacheConnectionManagerFactory;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.nio.AsyncClientConnectionManager;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.Method;
import org.apache.hc.core5.io.CloseMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static com.anubhav.Constants.*;

// http 2 is supported only in async clients
// one more observation was that if server does not support http2, then we start getting the cancellation error
// https://hc.apache.org/httpcomponents-client-5.1.x/migration-guide/migration-to-async-http2.html
public class ApacheAsyncClients {

    private static final Logger logger = LoggerFactory.getLogger(ApacheAsyncClients.class);

    public static void main(String[] args){
        run();
    }
    public static void run()  {

        CloseableHttpAsyncClient http1AsyncClient = getHttp1AsyncClient();
        http1AsyncClient.start();
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        IntStream.range(0, NumberOfLoops).parallel().forEach(x -> {
            try {
                executeRequest(http1AsyncClient);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch.stop();
        logger.info("total time taken for async http1 = {}", stopWatch.getTotalTimeMillis());
        http1AsyncClient.close(CloseMode.GRACEFUL);
        logger.info("****************************************************");

        CloseableHttpAsyncClient http2AsyncClient = getHttp2AsyncClient();
        http2AsyncClient.start();
        StopWatch stopWatch1 = new StopWatch();
        stopWatch1.start();
        IntStream.range(0, NumberOfLoops).parallel().forEach(x -> {
            try {
                executeRequest(http2AsyncClient);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        });
        stopWatch1.stop();
        http2AsyncClient.close(CloseMode.GRACEFUL);
        logger.info("total time taken for async http2 = {}", stopWatch1.getTotalTimeMillis());
    }

    private static void executeRequest(CloseableHttpAsyncClient httpAsyncClient) throws InterruptedException, ExecutionException, URISyntaxException {
        SimpleHttpRequest simpleHttpRequest = SimpleHttpRequest.create(Method.GET, new URI(uri));
        Future<?> future = httpAsyncClient.execute(
                simpleHttpRequest, new FutureCallback<>() {
                    @Override
                    public void completed(SimpleHttpResponse simpleHttpResponse) {
                        logger.debug("received response = "+ simpleHttpResponse.getBodyText());
                        logger.debug("response protocol= {}", simpleHttpResponse.getVersion());
                        simpleHttpResponse.headerIterator().forEachRemaining(x -> {
                            logger.debug(x.getName()+" = "+x.getValue());
                        });
                        logger.debug("*************");
                    }

                    @Override
                    public void failed(Exception e) {
                        logger.error("error " + e.getMessage());
                    }

                    @Override
                    public void cancelled() {
                        logger.error("cancelled");
                    }
                });
        future.get();
    }

    private static CloseableHttpAsyncClient getHttp2AsyncClient() {
        // note that http2 clients do not have connection manager. They manager internal map of routs and connection pools.
        return HttpAsyncClients.
                customHttp2().
                build();
    }

    private static CloseableHttpAsyncClient getHttp1AsyncClient() {
        AsyncClientConnectionManager connectionManager = ApacheConnectionManagerFactory.getAsyncClientManager(
                baseUri,
                port);

        return HttpAsyncClients.custom().setConnectionManager(connectionManager).build();
    }
}
