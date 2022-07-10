package com.anubhav.client.javaNative;
import com.anubhav.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;

import static com.anubhav.Constants.uri;

/*
Http 2 is supported but things like connection pooling is not supported hence one has to code connection pooling on
themselves
 */
public class JavaRestClient {

    private static final Logger logger = LoggerFactory.getLogger(JavaRestClient.class);

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException {

        List<HttpClient.Version> versionList = Arrays.asList(HttpClient.Version.HTTP_1_1, HttpClient.Version.HTTP_2);
        for(HttpClient.Version version: versionList){
            HttpClient httpClient = HttpClient.
                    newBuilder().
                    version(version).
                    build();
            HttpRequest httpRequest = HttpRequest.newBuilder().
                    uri(new URI(uri)).version(version).GET().
                    build();

            StopWatch stopWatch = new StopWatch();
            stopWatch.start();

            for(int i=0;i< Constants.NumberOfLoops;i++){
                var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
                logger.debug(response.body());
                logger.debug(response.version().toString());
            }

            stopWatch.stop();
            logger.info("total time taken with JAVA rest client {} = {}",version ,stopWatch.getTotalTimeMillis());

        }
    }
}
