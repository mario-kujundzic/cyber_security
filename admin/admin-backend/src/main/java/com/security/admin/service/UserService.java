package com.security.admin.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.security.admin.dto.ResetPasswordDTO;
import com.security.admin.dto.UserTokenStateDTO;
import com.security.admin.exception.OftenUsedPasswordException;
import com.security.admin.exception.UserException;
import com.security.admin.model.User;
import com.security.admin.repository.UserRepository;
import com.security.admin.security.CustomUserDetailsService;
import com.security.admin.security.TokenUtils;
import com.security.admin.util.RandomUtility;

@Service
public class UserService {

	private UserRepository userRepository;
	private TokenUtils tokenUtils;
	private AuthenticationManager authenticationManager;
	private MailSenderService mailSenderService;
	private CustomUserDetailsService userDetailsService;

	@Autowired
	public UserService(UserRepository userRepository, TokenUtils tokenUtils,
			AuthenticationManager authenticationManager, MailSenderService mailSenderService,
			CustomUserDetailsService userDetailsService) {
		this.userRepository = userRepository;
		this.tokenUtils = tokenUtils;
		this.authenticationManager = authenticationManager;
		this.mailSenderService = mailSenderService;
		this.userDetailsService = userDetailsService;
	}

	public User findByUsername(String username) {
		return userRepository.findByUsername(username);
	}

	public UserTokenStateDTO login(String username, String password) throws UserException {
		User existUser = null;
		try {
			existUser = getOne(username);
		} catch (NoSuchElementException e) {
			throw new UserException("No such element!", "username", "User with this username doesn't exist.");
		}

		if (!existUser.isEnabled()) {
			throw new DisabledException("Your account hasn't been activated yet. Please check your email!");
		}

		UserTokenStateDTO token = generateToken(username, password);
		return token;
	}

	public User getOne(String username) throws NoSuchElementException {
		User user = findByUsername(username);
		if (user == null) {
			throw new NoSuchElementException("User with this username doesn't exist!");
		}
		return user;
	}

	public UserTokenStateDTO generateToken(String username, String password) throws UserException {
		Authentication authentication = null;
		try {
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (BadCredentialsException e) {
			throw new UserException("Bad credentials exception!", "password", "Incorrect password.");
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		// create token
		User user = (User) authentication.getPrincipal();
		String jwt = tokenUtils.generateToken(user.getUsername());
		int expiresIn = tokenUtils.getExpiredIn();
		String role = user.getRoles().get(0).getName();

		return new UserTokenStateDTO(user.getId(), jwt, expiresIn, user.getUsername(), user.getName(),
				user.getSurname(), role);
	}

	public void forgotPassword(String username) {
		User user = getOne(username);
		if (!user.isEnabled()) {
			throw new DisabledException("Your account hasn't been activated yet. Please check your email first!");
		}
		String generatedKey = RandomUtility.buildAuthString(30);
		user.setResetKey(generatedKey);
		mailSenderService.forgotPassword(user.getUsername(), generatedKey);
		userRepository.save(user);
	}

	public UserTokenStateDTO resetPassword(ResetPasswordDTO dto) throws UserException, OftenUsedPasswordException {
		User user = userRepository.findByResetKey(dto.getResetKey());
		if (user == null) {
			throw new NoSuchElementException("The password is already reset or the link is invalid!");
		}
		String newPassword = dto.getNewPassword();
		if (oftenUsedPassword(newPassword)) {
			throw new OftenUsedPasswordException("The password is often used and therefore weak!");
		}
		if (passwordContainsUserData(newPassword, user)) {
			throw new OftenUsedPasswordException("The password should not contain your personal information!");
		}
		userDetailsService.changePasswordUtil(user, newPassword);
		user.setResetKey(null);
		userRepository.save(user);

		UserTokenStateDTO token = generateToken(user.getUsername(), newPassword);
		return token;
	}
	
	private boolean oftenUsedPassword(String password) {
		List<String> oftenUsedPasswords = new ArrayList<>();
	    try (Stream<String> lines = Files.lines(Paths.get("./src/main/resources/common-passwords.txt"))) {
	    	oftenUsedPasswords = lines.collect(Collectors.toList());
	    } catch (IOException e) {
			e.printStackTrace();
		}
	    
	    if (oftenUsedPasswords.contains(password.toLowerCase()))
	    	return true;
	    return false;
	}
	
	private boolean passwordContainsUserData(String password, User user) {
		String lowerPass = password.toLowerCase();
		if (lowerPass.contains(user.getName().toLowerCase()))
			return true;
		if (lowerPass.contains(user.getSurname().toLowerCase()))
			return true;
		if (lowerPass.contains(user.getUsername().split("@")[0].toLowerCase()))
			return true;
		return false;
	}
}
