package com.worksap.stm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.worksap.stm.lib.ScheduleCreateRequestEntity;
import com.worksap.stm.lib.ScheduleResponseEntity;
import com.worksap.stm.lib.response.ApiResponseEntity;

@RestController
public class ScheduleController {
	
	@Autowired 
	SchedulerService schedulerService;
	
	@RequestMapping(path = "/schedule", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<ScheduleResponseEntity> createSchedule(@RequestBody ScheduleCreateRequestEntity schedule) {
		return schedulerService.createSchedule(schedule);
	}

	@RequestMapping(path = "/schedule/{scheduleId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<ScheduleResponseEntity> getSingleSchedule(@PathVariable String scheduleId) {
		return schedulerService.getSingleSchedule(scheduleId);
	}

	@RequestMapping(path = "/schedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllSchedule() {
		return schedulerService.getAllSchedule();
	}

	@RequestMapping(path = "/user/{userId}/schedule", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUser(@PathVariable String userId) {
		return schedulerService.getAllScheduleForUser(userId);
	}

	@RequestMapping(path = "/user/{userId}/schedule/day/{date}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUserInDay(@PathVariable String userId,
			@PathVariable String date) {
		return schedulerService.getAllScheduleForUserInDay(userId, date);
	}

	@RequestMapping(path = "/user/{userId}/schedule/week/{weekStartDate}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUserInWeek(@PathVariable String userId,
			@PathVariable String weekStartDate) {
		return schedulerService.getAllScheduleForUserInWeek(userId, weekStartDate);
	}

	@RequestMapping(path = "/user/{userId}/schedule/month/{month}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUserInMonth(@PathVariable String userId,
			@PathVariable String month) {
		return schedulerService.getAllScheduleForUserInMonth(userId, month);
	}
}
