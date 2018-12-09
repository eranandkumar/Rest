package com.rest.ws.service;

import com.rest.ws.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto user);
	UserDto getUserByEmail(String email);
	UserDto getUserByUserId(String userId);
	UserDto updateUser(String id, UserDto userDto);
	String deleteUserById(String id);

	List<UserDto> getAllUsers(int page, int limit);
}
