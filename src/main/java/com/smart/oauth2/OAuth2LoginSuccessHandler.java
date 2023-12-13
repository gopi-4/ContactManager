package com.smart.oauth2;

import com.smart.entities.User;
import com.smart.enums.AuthenticationProvider;
import com.smart.enums.Role;
import com.smart.repository.UserRepository;
import com.smart.service.ImageService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Date;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler{

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ImageService imageService;
	
	@Override
	public void onAuthenticationSuccess(HttpServletRequest request,
										HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {

		CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
		String emailString = oAuth2User.getName();
		String nameString = oAuth2User.getFullName();
		String authProvider = oAuth2User.getAuthProvider();
		User user = userRepository.getUserByEmail(emailString).orElse(null);

		if(user==null) {

			User newUser = new User();
			newUser.setName(nameString);
			newUser.setEmail(emailString);
			newUser.setPassword(nameString+"+"+emailString);
			newUser.setRole(Role.ROLE_USER);
			newUser.setEnabled(true);
			newUser.setStatus(false);
			newUser.setDate(new Date().toString());
			newUser.setImage(imageService.getDefault());
			
			if (authProvider.equals("Facebook")) {
				newUser.setAuthProvider(AuthenticationProvider.FACEBOOK);
			}else if (authProvider.equals("Google")) {
				newUser.setAuthProvider(AuthenticationProvider.GOOGLE);
			}else {
				newUser.setAuthProvider(AuthenticationProvider.GITHUB);
			}
			
			this.userRepository.save(newUser);
			logger.info("New User Created.");
		}else {
			if (authProvider.equals("Facebook")) {
				user.setAuthProvider(AuthenticationProvider.FACEBOOK);
			}else if (authProvider.equals("Google")) {
				user.setAuthProvider(AuthenticationProvider.GOOGLE);
			}else {
				user.setAuthProvider(AuthenticationProvider.GITHUB);
			}
			user.setStatus(true);
			this.userRepository.save(user);
			logger.info("User Exists.");
		}

		response.sendRedirect("/redirect/");
		super.onAuthenticationSuccess(request, response, authentication);
	}

}
