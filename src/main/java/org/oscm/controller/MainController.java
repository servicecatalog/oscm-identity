package org.oscm.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @GetMapping
    public String homePage(){
        return "Welcome to the oscm-identity home page";
    }

}
