package com.worksap.stm.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;
import org.springframework.stereotype.Service;

import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Statement;
import com.datastax.driver.core.utils.UUIDs;
import com.worksap.stm.lib.UserCreateRequestEntity;
import com.worksap.stm.lib.UserResponseEntity;
import com.worksap.stm.lib.response.ApiFailureCause;
import com.worksap.stm.lib.response.ApiResponseEntity;

import ch.qos.logback.core.net.SyslogOutputStream;

@Service
public class UserService {

	@Autowired
	private CqlTemplate cqltemplate;
	
	public ApiResponseEntity<UserResponseEntity> createUser(UserCreateRequestEntity user) {
		
		ApiResponseEntity<UserResponseEntity> finalresult = new ApiResponseEntity<>();
		
		
		if(user.getName()==null) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.NAME_NOT_SPECIFIED);
			return finalresult;
		}
		
	
		if(user.getEmail()==null) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.EMAIL_NOT_SPECIFIED);
			return finalresult;
		}
		
		
		String EMAIL_REGEX = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		Boolean b = user.getEmail().matches(EMAIL_REGEX);
		if(b==false) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.INVALID_EMAIL);
			return finalresult;
		}
		
		
		
		String query = "SELECT * FROM stm_practice_awesome_name.user where email='" + user.getEmail()+"';";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		
		if(ls.size()!=0) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.EMAIL_EXISTS);
			return finalresult;
		}
		
		
		
		String id = UUIDs.timeBased().toString();
		
		UserResponseEntity u = UserResponseEntity.builder().userId(id).email(user.getEmail()).name(user.getName()).build();
		PreparedStatement preparedStatement = cqltemplate.getSession().prepare("INSERT INTO stm_practice_awesome_name.user(userId,email,name) values(?,?,?);");
		
		Statement insertstatement = preparedStatement.bind(u.getUserId(),u.getEmail(),u.getName());
		
		cqltemplate.execute(insertstatement);
		
		finalresult.setStatus("SUCCESS");
		finalresult.setDetails(u);
		
		return finalresult;
	}

	public ApiResponseEntity<UserResponseEntity> getUser(String userId) {
		
		ApiResponseEntity<UserResponseEntity> finalresult = new ApiResponseEntity<>();
		
		
		String query = "SELECT userId,name,email FROM stm_practice_awesome_name.user where userId='" + userId +"';";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);
				
		if(ls.size()==0) {
			finalresult.setStatus("FAILURE");
			finalresult.setCause(ApiFailureCause.USER_NOT_FOUND);
		}
		else {
			finalresult.setStatus("SUCCESS");
			Row r = ls.get(0);
			UserResponseEntity u = new UserResponseEntity(r.getString(0),r.getString(1),r.getString(2));
			finalresult.setDetails(u);
		}
		
		return finalresult;
	}

	public List<UserResponseEntity> getAll() {
		
		List<UserResponseEntity> result = new ArrayList<>();
		
		String query = "SELECT userId,name,email FROM stm_practice_awesome_name.user;";
		ResultSet resultSet = cqltemplate.query(query);
		List<Row> ls = resultSet.all();
		System.out.println(ls);
		
		for(int i=0;i<ls.size();i++) {
			
			Row r = ls.get(i);
			System.out.println(r);
			result.add(new UserResponseEntity(r.getString(0),r.getString(1),r.getString(2)));
		}
		
		return result;
	}

}