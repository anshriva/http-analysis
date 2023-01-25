This topic talks about comparison of the performance between Http 1.1 & Http 2.1

Here is the short explanation on the Http protocols: 
1. Http 1.1 :  Makes one request and one response
2. Http 2:  Can make multiple request and response in the same connection. So, multiplexing is possible using stream id.  
3. Http 3:  uses UDP 

| Scenario                                              | time in ms for 10000 request | Explanation                                                                                                                                                              |
|-------------------------------------------------------|------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| JAVA rest client HTTP_1_1                             | 7492                         | Simple implementation                                                                                                                                                    |
| JAVA rest client HTTP_2_1                             | 4617                         | better because of Http 2                                                                                                                                                 |
| Apache sync rest client HTTP_1_1 with pooling support | 2400                         | manages the connection Better                                                                                                                                            |
| Apache sync rest client HTTP_1_1 with basic           | 4836                         | The request fails after 200 requests in series. Hence I had to reconnect the client after every 200 request. Having said that this is still faster than JAVA rest client |
| apache async rest client with HTTP 1_1                | 1472                         | the async sync client has the connection pooling and also I can make multiple parallel calls and client will take care of pooling etc.                                   |
| apache async rest client with HTTP_2                  | 1273                         | best performance because of the benefits of http2                                                                                                                        |




### My Recommendations
1. Java Rest Client:  Do not use it. It is slow. The only benefit is that it is easy to code.
2. apache Rest client with sync flow:
    1. Do not use it.
    2. It is slower than async flow.
    3. It is harder to migrate to http 2 because the http 2 is supported only in async flow
    4. Also, it is not easy to code like java REST and kept on giving me error after every 200 requests. There was no good doc to help me.
3. Apache Rest Client with async flow. USE this. It is much faster than other flows. It supports both http 1.1 and http2. Only downside is that, it takes a little more time to code, but definitely worth the effort.

### My observations
1. Most of the libraries are still in Http 1.1
2. Http 2 came in 2016, yet, I don't see many libraries supporting it. Apache support came only in Feb 2020.
3. Java itself came with Http 2 as GA in java 11, hence other java libraries also could support GA after that,
4. The spring boot server by default is http 1.1. If you want your server to support http2, then you need to specifically write following line the application.properties:

   ```server.http2.enabled=true```

5. In case server does not support http 2 and client request is made with http 2, then it will throw error. 