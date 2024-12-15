package com.odedia.cloud_lab;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    @Value("${helloworld.message:'Shalom Olam - default!'}")
    private String helloMessage;

    @RequestMapping("/")
    public String helloWorld(){
        return helloMessage;
    }
}