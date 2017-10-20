package com.worksap.stm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.worksap.stm.lib.UserCreateRequestEntity;
import com.worksap.stm.lib.UserResponseEntity;
import com.worksap.stm.lib.response.ApiResponseEntity;
import com.worksap.stm.lib.response.ApiStatus;

@RestController
public class UserController {
	//@Autowired UserService userService;
	
	@RequestMapping(path = "/user", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<UserResponseEntity> createUser(@RequestBody UserCreateRequestEntity user) {
		return null;
	}

	@RequestMapping(path = "/user/{userId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<UserResponseEntity> getUser(@PathVariable String userId) {
		return null;
	}

	@RequestMapping(path = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<List<UserResponseEntity>> getAllUser() {
		ApiResponseEntity<List<UserResponseEntity>> response = new ApiResponseEntity<>();
		try {
			response.setStatus(ApiStatus.SUCCESS);
			//response.setDetails(userService.getAll());
		} catch(Exception e) {
			response.setStatus(ApiStatus.FAILURE);
			response.setCause(e.getMessage());
		}
		return response;
	}
}
