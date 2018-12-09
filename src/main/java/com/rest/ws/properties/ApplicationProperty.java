package com.rest.ws.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperty {

    @Autowired
    Environment environment;

    public String getTokenSecret(){
        return environment.getProperty("tokenSecret");
    }
}
