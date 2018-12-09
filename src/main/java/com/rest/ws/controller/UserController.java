package com.rest.ws.controller;

import com.rest.ws.exception.ErrorMessages;
import com.rest.ws.exception.UserServiceException;
import com.rest.ws.model.request.UserDetailModel;
import com.rest.ws.model.response.AddressesRest;
import com.rest.ws.model.response.UserRespRest;
import com.rest.ws.service.AddressesService;
import com.rest.ws.service.UserService;
import com.rest.ws.shared.dto.AddressDTO;
import com.rest.ws.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Link;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
@RequestMapping("/users")
public class UserController {
	
	@Autowired
	UserService userService;

	@Autowired
    AddressesService addressesService;
	
	@GetMapping(value = "/{id}",
	produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public UserRespRest getUser(@PathVariable String id) {
		UserRespRest userRespRest = new UserRespRest();
		if (StringUtils.isEmpty(id)) throw new UsernameNotFoundException("UserId is Null");

		UserDto userDto = userService.getUserByUserId(id);
		BeanUtils.copyProperties(userDto, userRespRest);
		return userRespRest;
	}
	
	@PostMapping(
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
	)
	public UserRespRest createUser(@RequestBody UserDetailModel userDetails) throws Exception{
		UserRespRest userResponse = new UserRespRest();
		ModelMapper modelMapper = new ModelMapper();
		UserDto userDto = modelMapper.map(userDetails, UserDto.class);

		if(userDetails.getFirstName().isEmpty())
			throw new NullPointerException("The Firstname is Null");
		if(userDetails.getLastName().isEmpty())
			throw new UserServiceException(ErrorMessages.MISSING_REQUIRED_FIELD.getErrorMessage());


		UserDto createdUser = userService.createUser(userDto);

		userResponse = modelMapper.map(createdUser, UserRespRest.class);

		
		return userResponse;
	}
	
	@PutMapping( value = "/{id}",
			consumes = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE},
			produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE}
	)
	public UserRespRest updateUser(@PathVariable String id, @RequestBody UserDetailModel requestBoby) {
		UserRespRest updateResponse = new UserRespRest();
		UserDto userDto = new UserDto();

		BeanUtils.copyProperties(requestBoby, userDto);
		UserDto createdUser = userService.updateUser(id, userDto);
		BeanUtils.copyProperties(createdUser, updateResponse);

		return updateResponse;
	}
	
	@DeleteMapping(value = "/{id}")
	public String deleteUser(@PathVariable String id) {

		if (StringUtils.isEmpty(userService.deleteUserById(id)))
			return "User Id deleted, ID --> ".concat(id);

		return "User not Deleted".concat(id);

	}

	@GetMapping(
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE}
	)
	public List<UserRespRest> getUserWithPageAndLimit(@RequestParam(value = "page", defaultValue = "0") int page,
													  @RequestParam(value = "limit", defaultValue = "25") int limit){
		List<UserRespRest> returnedResponse = new ArrayList<>();

		List<UserDto> listofUsers = userService.getAllUsers(page, limit);

		for (UserDto userDto : listofUsers) {
			UserRespRest userRespRest = new UserRespRest();
			BeanUtils.copyProperties(userDto, userRespRest);
			returnedResponse.add(userRespRest);
		}
		return returnedResponse;
	}

	@GetMapping(value = "/{id}/addresses",
			produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
	public List<AddressesRest> getUserAddresses(@PathVariable String id) {
		List<AddressesRest> responseValue = new ArrayList<>();
		if (StringUtils.isEmpty(id)) throw new UsernameNotFoundException("UserId is Null");

		List<AddressDTO> addressDto = addressesService.getAddresses(id);
		if(addressDto != null && !addressDto.isEmpty()) {
            Type listType = new TypeToken<List<AddressesRest>>() {
            }.getType();
            responseValue = new ModelMapper().map(addressDto, listType);

            for (AddressesRest addressesRest : responseValue) {
                Link userLink = linkTo(methodOn(UserController.class).getUser(id)).withRel("User");
                Link addressLink = linkTo(methodOn(UserController.class).getUserAddressById(id, addressesRest.getAddressId())).withSelfRel();
                addressesRest.add(userLink);
                addressesRest.add(addressLink);
            }
        }
		return responseValue;
	}

    @GetMapping(value = "/{userId}/addresses/{addressId}",
            produces = {MediaType.APPLICATION_XML_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public AddressesRest getUserAddressById(@PathVariable String userId, @PathVariable String addressId) {

        if (StringUtils.isEmpty(addressId)) throw new UsernameNotFoundException("AddressId is Null");

        AddressDTO addressDto = addressesService.getAddressById(addressId);

        //Hateos Impl
        //Link addressLink = linkTo(UserController.class).slash(userId).slash("addresses").slash(addressId).withSelfRel();
        //Link userLink = linkTo(UserController.class).slash(userId).withRel("User");
        //Link addressesLink = linkTo(UserController.class).slash(userId).slash("addresses").withRel("Addresses");
        AddressesRest responseValue = new ModelMapper().map(addressDto, AddressesRest.class);

        Link addressLink = linkTo(methodOn(UserController.class).getUserAddressById(userId, addressId)).withSelfRel();
        Link userLink = linkTo(methodOn(UserController.class).getUser(userId)).withRel("User");
        Link addressesLink = linkTo(methodOn(UserController.class).getUserAddresses(userId)).withRel("Addresses");

        responseValue.add(addressLink);
        responseValue.add(userLink);
        responseValue.add(addressesLink);

        return responseValue;
    }
}
