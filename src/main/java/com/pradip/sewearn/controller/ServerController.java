package com.pradip.sewearn.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@CrossOrigin(origins = "*")
public class ServerController {
    @GetMapping("/")
    public String getIndex(){
        return "Server is running......";
    }

    @GetMapping("/keep-alive")
    @ResponseBody
    public String getKeepAlive(){
        return "Server is running......";
    }
}
