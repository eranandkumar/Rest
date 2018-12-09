package com.rest.ws.service.impl;

import com.rest.ws.exception.ErrorMessages;
import com.rest.ws.exception.UserServiceException;
import com.rest.ws.model.response.ErrorMessage;
import com.rest.ws.shared.dto.AddressDTO;
import com.rest.ws.shared.utils.Utils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.rest.ws.io.entity.UserEntity;
import com.rest.ws.repository.UserRepository;
import com.rest.ws.service.UserService;
import com.rest.ws.shared.dto.UserDto;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
	
	@Autowired
	UserRepository userRepo;
	@Autowired
	Utils utils;
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	@Override
	public UserDto createUser(UserDto user) {

		if(userRepo.findByEmail(user.getEmail()) != null)
				throw new RuntimeException("User Already Exist");

		for (int i=0; i<user.getAddresses().size(); i++){
			AddressDTO addressDTO = user.getAddresses().get(i);
			addressDTO.setUserDetails(user);
			addressDTO.setAddressId(utils.generateAddressId(30));
			user.getAddresses().set(i, addressDTO);
		}

		ModelMapper modelMapper = new ModelMapper();
		user.setEncryptedPassword(bCryptPasswordEncoder.encode(user.getPassword()));

		String generatedUserId = utils.generateUserId(30);
		user.setUserId(generatedUserId);
		UserEntity userEntity = modelMapper.map(user, UserEntity.class);
		UserEntity savedEntiry = userRepo.save(userEntity);
		UserDto returnedDto = modelMapper.map(savedEntiry, UserDto.class);

		return returnedDto;
	}

	@Override
	public UserDto getUserByEmail(String email){
		UserDto responseDto = new UserDto();
		UserEntity userEntity = userRepo.findByEmail(email);

		if (userEntity ==null) throw new UsernameNotFoundException("User not exist "+email);

		BeanUtils.copyProperties(userEntity, responseDto);
		return responseDto;
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserDto responseDto = new UserDto();
		UserEntity userEntity = userRepo.findByuserId(userId);

		if (userEntity ==null) throw new UsernameNotFoundException("User Id not Found  --> " +userId);

		BeanUtils.copyProperties(userEntity, responseDto);
		return responseDto;
	}

	@Override
	public UserDto updateUser(String id, UserDto userDto) {
		UserDto responseDto = new UserDto();
		UserEntity userEntity = userRepo.findByuserId(id);

		if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		if (!StringUtils.isEmpty(userDto.getFirstName())){
			userEntity.setFirstName(userDto.getFirstName());
		}
		if (!StringUtils.isEmpty(userDto.getLastName())){
			userEntity.setLastName(userDto.getLastName());
		}

		userRepo.save(userEntity);
		BeanUtils.copyProperties(userEntity, responseDto);


		return responseDto;
	}

	@Override
	public String deleteUserById(String id) {
		UserEntity userEntity = userRepo.findByuserId(id);

		if (userEntity == null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());

		userRepo.delete(userEntity);

		return null;
	}

	@Override
	public List<UserDto> getAllUsers(int page, int limit) {
		List<UserDto> returnedResponse = new ArrayList<>();
		Pageable pageableReq = PageRequest.of(page, limit);

		Page<UserEntity> userPages = userRepo.findAll(pageableReq);
		for (UserEntity pageReq : userPages) {
			UserDto userDto = new UserDto();
			BeanUtils.copyProperties(pageReq, userDto);
			returnedResponse.add(userDto);
		}
		return returnedResponse;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		UserEntity userEntity = userRepo.findByEmail(email);

		if (userEntity ==null) throw new UsernameNotFoundException("User not exist "+email);

		return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
	}
}
