package com.smart.controllers;

import com.smart.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private RestService restService;
    @GetMapping("/logOut/{userId}")
    public void logOut(@PathVariable("userId") Integer userId) {
        this.restService.logout(userId);
    }

    @GetMapping("/updateContactStatus/{email}/{status}")
    public void updateContactStatusByUserEmail(@PathVariable("email") String email, @PathVariable("status") Boolean status) {
        this.restService.updateContactStatusByUserEmail(email, status);
    }
}
