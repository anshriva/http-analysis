package com.anubhav;

public class Constants {
    private static String domain = "localhost";
    private static String protocol = "http";
    private static String hello= "hello";


    public static int NumberOfLoops =  10000;
    public static int port = 8080;
    public static String baseDomain = protocol+"://"+ domain;
    public static String baseUri = baseDomain +":"+port;
    public static String uri = baseUri +"/"+hello;


}
