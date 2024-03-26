package com.smart.controllers;

import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.service.RestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private RestService restService;

    @PostMapping("/logOut")
    public void logOut(@RequestBody User user) {
        this.restService.logout(user);
    }

    @GetMapping("/contactRegistrationStatus/{email}/{status}")
    public void updateRegistrationStatus(@PathVariable("email") String email, @PathVariable("status") Boolean status) {
        this.restService.updateRegistrationStatus(email, status);
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

    @PostMapping("/saveAllContacts")
    public void saveAllContacts(@RequestBody List<Contact> contacts) {
        this.restService.saveAllContacts(contacts);
    }

    @PostMapping("/saveAllUsers")
    public void saveAllUsers(@RequestBody List<User> users) { this.restService.saveAllUsers(users); }
}
