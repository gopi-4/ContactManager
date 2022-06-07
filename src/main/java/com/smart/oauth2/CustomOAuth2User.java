package com.smart.oauth2;

import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class CustomOAuth2User implements OAuth2User {

	private OAuth2User oauth2User;
	private String authProvider;
	
	public CustomOAuth2User(OAuth2User oauth2User, String authProvider) {
		this.oauth2User = oauth2User;
		this.authProvider = authProvider;
	}

	@Override
	public Map<String, Object> getAttributes() {
		return oauth2User.getAttributes();
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return oauth2User.getAuthorities();
	}

	@Override
	public String getName() {
		return oauth2User.getAttribute("email");
	}
	
	public String getFullName() {
		return oauth2User.getAttribute("email");
	}
	
	public String getAuthProvider() {
		return this.authProvider;
	}
	
	public String getImage() {
		return oauth2User.getAttribute("picture");
	}
	
	public String getFirstName() {
		return oauth2User.getAttribute("first_name");
	}
	
	public String getlastName() {
		return oauth2User.getAttribute("last_name");
	}
	
	public String getMiddleName() {
		return oauth2User.getAttribute("middle_name");
	}
	
	public String getShortName() {
		return oauth2User.getAttribute("short_name");
	}
	
	public String getNameFormat() {
		return oauth2User.getAttribute("name_format");
	}

	public String getAbout() {
		return oauth2User.getAttribute("about");
	}
}