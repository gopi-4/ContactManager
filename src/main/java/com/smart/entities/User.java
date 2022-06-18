package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonIgnore;


@Entity
@Table(name = "USERS")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int Id;
	@NotBlank(message = "Name required!!")
	@Size(min = 3, max = 50, message = "*Name should be greater than 3 chars and less than 50 chars.")
	private String name;
	@NotBlank(message = "Email required!!")
	@Email(message = "email is not correct.")
	@Column(unique = true)
	private String email;
	@NotBlank(message = "Password required!!")
	@Size(min = 5, message = "Password should not be less than 5 chars.")
	private String password;
	private String role;
	@Lob
	private String image;
	@Column(length = 500)
	private String about;
	private boolean enabled;
	@JsonIgnore
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "user", orphanRemoval = true)
	private List<Contact> contacts = new ArrayList<>();
	private long coins;
	private String date;
	private boolean status;
	@Enumerated(EnumType.STRING)
	@Column(name = "auth_provider")
	private AuthenticationProvider authProvider;
	
	public User() {
		super();
	}
	
	
	
	public int getId() {
		return Id;
	}



	public void setId(int id) {
		Id = id;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getRole() {
		return role;
	}



	public void setRole(String role) {
		this.role = role;
	}



	public String getImage() {
		return image;
	}



	public void setImage(String image) {
		this.image = image;
	}



	public String getAbout() {
		return about;
	}



	public void setAbout(String about) {
		this.about = about;
	}



	public boolean isEnabled() {
		return enabled;
	}



	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}



	public List<Contact> getContacts() {
		return contacts;
	}



	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}



	public long getCoins() {
		return coins;
	}



	public void setCoins(long coins) {
		this.coins = coins;
	}



	public String getDate() {
		return date;
	}



	public void setDate(String date) {
		this.date = date;
	}



	public boolean getStatus() {
		return status;
	}



	public void setStatus(boolean status) {
		this.status = status;
	}



	public AuthenticationProvider getAuthProvider() {
		return authProvider;
	}



	public void setAuthProvider(AuthenticationProvider authProvider) {
		this.authProvider = authProvider;
	}



	@Override
	public String toString() {
		return "User [Id=" + Id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", image=" + image + ", about=" + about + ", enabled=" + enabled + ", contacts=" + contacts
				+ ", coins=" + coins + ", date=" + date + ", status=" + status + ", authProvider=" + authProvider + "]";
	}
}
