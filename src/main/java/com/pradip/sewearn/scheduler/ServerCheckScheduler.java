package com.pradip.sewearn.scheduler;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;

@Component
public class ServerCheckScheduler {
    private RestTemplate restTemplate = new RestTemplate();

     @Value("${server.url}")
    private String apiUrl;

    @Scheduled(fixedDelay = 600000) // 10 minutes in milliseconds600000
    public void callApi() {
        try{
            String response = restTemplate.getForObject(apiUrl+"/sewearn/keep-alive", String.class);
            System.out.println("\n=> Response from server : "+response+".");
//            String[] timeDate = LocalDateTime.now().toString().split("T");
//            System.out.println("=> Current Time         : "+timeDate[1].substring(0,timeDate[1].indexOf("."))+"");
//            System.out.println("=> Current Date         : "+timeDate[0]+"\n");
        } catch (Exception ex){
            System.out.println("Error in scheduler "+ex.getMessage());
        }
    }
}