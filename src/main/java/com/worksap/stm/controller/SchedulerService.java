package com.worksap.stm.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.utils.UUIDs;
import com.worksap.stm.lib.ScheduleCreateRequestEntity;
import com.worksap.stm.lib.ScheduleResponseEntity;
import com.worksap.stm.lib.UserCreateRequestEntity;
import com.worksap.stm.lib.UserResponseEntity;
import com.worksap.stm.lib.response.ApiFailureCause;
import com.worksap.stm.lib.response.ApiResponseEntity;

@Service
public class SchedulerService {

	@Autowired
	private CqlTemplate cqltemplate;

	public ApiResponseEntity<ScheduleResponseEntity> createSchedule(ScheduleCreateRequestEntity schedule) {

		ApiResponseEntity<ScheduleResponseEntity> finalresult = new ApiResponseEntity<>();

		// title not specified
		if (schedule.getTitle() == null || schedule.getTitle().isEmpty()) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.TITLE_NOT_SPECIFIED);
			return finalresult;
		}
		
		//System.out.println(Long.valueOf(schedule.getStartTime()));
		//System.out.println(schedule.getStartTime());
		// start time not specified
		if (Long.valueOf(schedule.getStartTime()) == null || Long.valueOf(schedule.getStartTime()) == 0) {
			System.out.println("this is the start time");
			System.out.println(Long.valueOf(schedule.getStartTime()));
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.START_TIME_NOT_SPECIFIED);
			return finalresult;
		}

		// end time not specified
		if (Long.valueOf(schedule.getEndTime()) == null  || Long.valueOf(schedule.getEndTime()) == 0) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.END_TIME_NOT_SPECIFIED);
			return finalresult;
		}

		// users not specified
		if (schedule.getUsers() == null || schedule.getUsers().isEmpty()) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.USERS_NOT_SPECIFIED);
			return finalresult;
		}

		// users not found, userID not found
		for (int i = 0; i < schedule.getUsers().size(); i++) {
			System.out.println(schedule.getUsers());
			String s = schedule.getUsers().get(i);
			String query = "SELECT * FROM stm_practice_awesome_name.user where userId='" + s + "';";
			ResultSet resultSet = cqltemplate.query(query);
			List<Row> ls = resultSet.all();

			if (ls.size() == 0) {
				finalresult.setStatus("FAILURE");
				finalresult.setCause(ApiFailureCause.USER_NOT_FOUND);
				return finalresult;
			}
		}

		// INVALID_START_TIME
		Timestamp t = new Timestamp(schedule.getStartTime());
		System.out.println("this is timestamp");
		System.out.println(t);
		
		
		java.util.Date date= new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));
		Timestamp cur = new Timestamp(date.getTime());
		
		
		if (schedule.getStartTime() < 0 || t.before(cur)) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.INVALID_START_TIME);
			return finalresult;
		}
		
		
		Timestamp t2 = new Timestamp(schedule.getEndTime());
		//System.out.println("this is timestamp");
		//System.out.println(t);
		
		
		java.util.Date date2= new java.util.Date();
     //   System.out.println(new Timestamp(date2.getTime()));
		Timestamp cur2 = new Timestamp(date2.getTime());
		// INVALID_END_TIME
		if (schedule.getEndTime() < 0 || schedule.getEndTime() <= schedule.getStartTime() || t2.before(cur2)) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.INVALID_END_TIME);
			return finalresult;
		}

		// SCHEDULE_CONFLICT
		for (int i = 0; i < schedule.getUsers().size(); i++) {
			System.out.println(schedule.getUsers());
			String query = "SELECT userId, scheduleid, starttime, endtime, title, description FROM stm_practice_awesome_name.scheduleUsers where userId = '"
					+ schedule.getUsers().get(i) + "';";
			ResultSet resultSet = cqltemplate.query(query);
			List<Row> ls = resultSet.all();

			for (int j = 0; j < ls.size(); j++) {
				Row r = ls.get(j);
				long st = r.getLong(2);
				long en = r.getLong(3);
				// new sch ka end< start
				if ((st<=schedule.getStartTime() && en >= schedule.getStartTime()) ||  (st>=schedule.getStartTime() && st<=schedule.getEndTime() && en<=schedule.getEndTime()) || (st<=schedule.getStartTime()&& en<=schedule.getEndTime()) || (st>=schedule.getStartTime() && st<=schedule.getEndTime() && en>=schedule.getEndTime())) {

				} else {
					finalresult.setStatus("FAILURE");
					finalresult.setCause(ApiFailureCause.SCHEDULE_CONFLICT);
					return finalresult;
				}

			}

		}

		// schedule is correct
		String id = UUIDs.timeBased().toString();

		ScheduleResponseEntity s = ScheduleResponseEntity.builder().scheduleId(id).startTime(schedule.getStartTime())
				.endTime(schedule.getEndTime()).title(schedule.getTitle()).users(schedule.getUsers())
				.description(schedule.getDescription()).build();
		PreparedStatement preparedStatement = cqltemplate.getSession().prepare(
				"INSERT INTO stm_practice_awesome_name.schedule(scheduleId, startTime, endtime, title, users, description) values(?,?,?,?,?,?);");

		Statement insertstatement = preparedStatement.bind(s.getScheduleId(), s.getStartTime(), s.getEndTime(),
				s.getTitle(), s.getUsers(), s.getDescription());

		cqltemplate.execute(insertstatement);

		finalresult.setStatus("SUCCESS");
		finalresult.setDetails(s);

		// adding into scheduleUsers
		for (int i = 0; i < schedule.getUsers().size(); i++) {
			String str_userId = schedule.getUsers().get(i);
			PreparedStatement preparedStatement1 = cqltemplate.getSession().prepare(
					"INSERT INTO stm_practice_awesome_name.scheduleUsers(userId,startTime,endtime, scheduleId, title, description) values(?,?,?,?,?,?);");

			Statement insertstatement1 = preparedStatement1.bind(str_userId, s.getStartTime(), s.getEndTime(),
					s.getScheduleId(), s.getTitle(), s.getDescription());

			cqltemplate.execute(insertstatement1);

		}

		return finalresult;
	}

	public ApiResponseEntity<ScheduleResponseEntity> getSingleSchedule(String scheduleId) {
		ApiResponseEntity<ScheduleResponseEntity> finalresult = new ApiResponseEntity<>();

		// user not present
		String query = "SELECT scheduleid, starttime, endtime, title, users, description FROM stm_practice_awesome_name.schedule where scheduleId='"
				+ scheduleId + "';";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);

		if (ls.size() == 0) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
		} else {
			finalresult.setStatus("SUCCESS");
			Row r = ls.get(0);
			ScheduleResponseEntity s = new ScheduleResponseEntity(r.getString(0), r.getLong(1), r.getLong(2),
					r.getString(3), r.getList(4, String.class), r.getString(5));
			// UserResponseEntity u = new UserResponseEntity(r.getString(0), r.getString(1),
			// r.getString(2));
			finalresult.setDetails(s);
		}

		return finalresult;
	}

	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllSchedule() {
		List<ScheduleResponseEntity> result = new ArrayList<>();
		ApiResponseEntity<List<ScheduleResponseEntity>> a = new ApiResponseEntity<List<ScheduleResponseEntity>>();

		String query = "SELECT scheduleid, starttime, endtime, title, users, description FROM stm_practice_awesome_name.schedule;";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);

		if (ls.size() == 0) {
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
		}

		for (int i = 0; i < ls.size(); i++) {

			ApiResponseEntity<ScheduleResponseEntity> cur = new ApiResponseEntity<>();
			cur.setStatus("SUCCESS");

			Row r = ls.get(i);
			System.out.println(r);
			result.add(new ScheduleResponseEntity(r.getString(0), r.getLong(1), r.getLong(2), r.getString(3),
					r.getList(4, String.class), r.getString(5)));

			cur.setDetails(new ScheduleResponseEntity(r.getString(0), r.getLong(1), r.getLong(2), r.getString(3),
					r.getList(4, String.class), r.getString(5)));

		}

		a.setStatus("SUCCESS");
		a.setDetails(result);

		return a;
	}

	// schedules for user
	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUser(String userId) {

		List<ScheduleResponseEntity> result = new ArrayList<>();
		ApiResponseEntity<List<ScheduleResponseEntity>> a = new ApiResponseEntity<List<ScheduleResponseEntity>>();

		String query = "SELECT userId, scheduleid, starttime, endtime, title, description FROM stm_practice_awesome_name.scheduleUsers where userId = '"
				+ userId + "';";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);
		List<String> l = new ArrayList<String>();
		l.add(userId);

		if (ls.size() == 0) {
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
		}

		for (int i = 0; i < ls.size(); i++) {

			ApiResponseEntity<ScheduleResponseEntity> cur = new ApiResponseEntity<>();
			cur.setStatus("SUCCESS");

			Row r = ls.get(i);

			System.out.println("printing row in schdule for users");
			System.out.println(r);

			result.add(new ScheduleResponseEntity(r.getString(1), r.getLong(2), r.getLong(3), r.getString(4), l,
					r.getString(5)));

			/*
			 * result.add(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 * 
			 * cur.setDetails(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 */

		}

		a.setStatus("SUCCESS");
		a.setDetails(result);

		return a;
	}

	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUserInDay(String userId, String date) {
		List<ScheduleResponseEntity> result = new ArrayList<>();
		ApiResponseEntity<List<ScheduleResponseEntity>> a = new ApiResponseEntity<List<ScheduleResponseEntity>>();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		long millis = 0;
		try {
			Date d = f.parse(date);
			millis = d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
			return a;
		}

		String query = "SELECT userId, scheduleid, starttime, endtime, title, description FROM stm_practice_awesome_name.scheduleUsers where userId = '"
				+ userId + "', startTime > " + millis + ", endTime < " + millis + 86400000 + " ;";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);
		List<String> l = new ArrayList<String>();
		l.add(userId);

		if (ls.size() == 0) {
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
		}

		for (int i = 0; i < ls.size(); i++) {

			ApiResponseEntity<ScheduleResponseEntity> cur = new ApiResponseEntity<>();
			cur.setStatus("SUCCESS");

			Row r = ls.get(i);

			System.out.println("printing row in schdule for users");
			System.out.println(r);

			result.add(new ScheduleResponseEntity(r.getString(1), r.getLong(2), r.getLong(3), r.getString(4), l,
					r.getString(5)));

			/*
			 * result.add(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 * 
			 * cur.setDetails(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 */

		}

		a.setStatus("SUCCESS");
		a.setDetails(result);

		return a;
	}

	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUserInWeek(String userId, String date) {
		List<ScheduleResponseEntity> result = new ArrayList<>();
		ApiResponseEntity<List<ScheduleResponseEntity>> a = new ApiResponseEntity<List<ScheduleResponseEntity>>();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
		long millis = 0;
		try {
			Date d = f.parse(date);
			millis = d.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
			return a;
		}

		String query = "SELECT userId, scheduleid, starttime, endtime, title, description FROM stm_practice_awesome_name.scheduleUsers where userId = '"
				+ userId + "', startTime > " + millis + ", endTime < " + millis + (7 * 86400000) + " ;";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);
		List<String> l = new ArrayList<String>();
		l.add(userId);

		if (ls.size() == 0) {
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
		}

		for (int i = 0; i < ls.size(); i++) {

			ApiResponseEntity<ScheduleResponseEntity> cur = new ApiResponseEntity<>();
			cur.setStatus("SUCCESS");

			Row r = ls.get(i);

			System.out.println("printing row in schdule for users");
			System.out.println(r);

			result.add(new ScheduleResponseEntity(r.getString(1), r.getLong(2), r.getLong(3), r.getString(4), l,
					r.getString(5)));

			/*
			 * result.add(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 * 
			 * cur.setDetails(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 */

		}

		a.setStatus("SUCCESS");
		a.setDetails(result);

		return a;
	}

	public ApiResponseEntity<List<ScheduleResponseEntity>> getAllScheduleForUserInMonth(String userId, String date) {
		List<ScheduleResponseEntity> result = new ArrayList<>();
		ApiResponseEntity<List<ScheduleResponseEntity>> a = new ApiResponseEntity<List<ScheduleResponseEntity>>();
		SimpleDateFormat f = new SimpleDateFormat("yyyy-mm");
		long millis1 = 0;
		long millis2 = 0;
		try {
			Date d = f.parse(date);
			millis1 = d.getTime();
			Calendar calendar = Calendar.getInstance();
			calendar.clear();

			calendar.set(Calendar.MONTH, Integer.parseInt(date.substring(5, 7)));
			calendar.set(Calendar.YEAR, Integer.parseInt(date.substring(0, 4)));

			calendar.add(Calendar.MONTH, 1);
			Date da = calendar.getTime();
			millis2 = da.getTime();
			System.out.println();
		} catch (ParseException e) {
			e.printStackTrace();
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
			return a;
		}

		String query = "SELECT userId, scheduleid, starttime, endtime, title, description FROM stm_practice_awesome_name.scheduleUsers where userId = '"
				+ userId + "', startTime > " + millis1 + ", endTime < " + millis2 + " ;";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);
		List<String> l = new ArrayList<String>();
		l.add(userId);

		if (ls.size() == 0) {
			a.setStatus("FAILURE");
			a.setCause(ApiFailureCause.SCHEDULE_NOT_FOUND);
		}

		for (int i = 0; i < ls.size(); i++) {

			ApiResponseEntity<ScheduleResponseEntity> cur = new ApiResponseEntity<>();
			cur.setStatus("SUCCESS");

			Row r = ls.get(i);

			System.out.println("printing row in schdule for users");
			System.out.println(r);

			result.add(new ScheduleResponseEntity(r.getString(1), r.getLong(2), r.getLong(3), r.getString(4), l,
					r.getString(5)));

			/*
			 * result.add(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 * 
			 * cur.setDetails(new ScheduleResponseEntity(r.getString(0), r.getLong(1),
			 * r.getLong(2), r.getString(3), r.getList(4, String.class), r.getString(5)));
			 */

		}

		a.setStatus("SUCCESS");
		a.setDetails(result);

		return a;
	}

	// long mills = date.getTime();

}
