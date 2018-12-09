package com.rest.ws.security;

import com.rest.ws.SpringApplicationContext;
import com.rest.ws.properties.ApplicationProperty;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 86400000;//10 Days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/users";

    public static String getTokenSecret(){
        ApplicationProperty applicationProperty = (ApplicationProperty)SpringApplicationContext.getBean("applicationProperty");
        return applicationProperty.getTokenSecret();
    }
}
