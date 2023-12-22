package com.smart.controllers;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private RestService restService;
    @GetMapping("/logOut/{userId}")
    public void logOut(@PathVariable("userId") Integer userId) {
        this.restService.logout(userId);
    }

    @GetMapping("/updateContactStatus/{Id}/{status}")
    public void updateContactStatusByUserEmail(@PathVariable("Id") int Id, @PathVariable("status") Boolean status) {
        this.restService.updateContactStatusByUserEmail(Id, status);
    }

    @GetMapping("/contactRegistrationStatus/{email}")
    public void updateRegistrationStatus(@PathVariable("email") String email) {
        this.restService.updateRegistrationStatus(email);
    }

    @GetMapping("/getUser/{Id}")
    public User getUser(@PathVariable("Id") int Id) {
        return this.restService.getUser(Id);
    }

    @PostMapping("/updateUser")
    public User updateUser(@RequestBody User user) {
        return this.restService.updateUser(user);
    }

    @GetMapping("/deleteUser/{Id}")
    public void deleteUser(@PathVariable("Id") int Id) {
        this.restService.deleteUser(Id);
    }

    @GetMapping("/getContact/{Id}")
    public Contact getContact(@PathVariable("Id") int Id) {
        return this.restService.getContactById(Id);
    }

    @PostMapping("/updateContact")
    public Contact updateContact(@RequestBody Contact contact) {
        return this.restService.updateContact(contact);
    }
    @GetMapping("/deleteContact/{Id}")
    public void deleteContactById(@PathVariable("Id") int Id) {
        this.restService.deleteContactById(Id);
    }
}
