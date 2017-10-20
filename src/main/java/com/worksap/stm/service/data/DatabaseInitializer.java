package com.worksap.stm.service.data;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cassandra.core.CqlTemplate;
import org.springframework.stereotype.Service;

@Service
public class DatabaseInitializer {
	private CqlTemplate cqlTemplate;

	@Autowired
	public DatabaseInitializer(CqlTemplate cqlTemplate) {
		this.cqlTemplate = cqlTemplate;
		createKeyspace();
		createColumnFamilies();
	}

	private void createKeyspace() {
		String query1 = "DROP KEYSPACE IF EXISTS stm_practice_awesome_name;";
		this.cqlTemplate.execute(query1);

		String query2 = "CREATE KEYSPACE stm_practice_awesome_name WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };";
		this.cqlTemplate.execute(query2);
	}
	
	private void createColumnFamilies() {
		String query1 = "DROP TABLE IF EXISTS stm_practice_awesome_name.user;";
		this.cqlTemplate.execute(query1);
		
		String query2 = "CREATE TABLE stm_practice_awesome_name.user(userId text PRIMARY KEY, email text, name text);";
		this.cqlTemplate.execute(query2);
		
		String query3 = "create index if not exists email_index on stm_practice_awesome_name.user(email);";
		this.cqlTemplate.execute(query3);
		
		String query4 = "DROP TABLE IF EXISTS stm_practice_awesome_name.schedule;";
		this.cqlTemplate.execute(query4);
		
		/*String query4_1 = "CREATE TYPE user_type (\r\n" + 
				"  userId text,\r\n" + 
				"  email text,\r\n" + 
				"  name text,\r\n" + 
				");";
		this.cqlTemplate.execute(query4_1);*/
		
		String query5 = "CREATE TABLE stm_practice_awesome_name.schedule(scheduleId text PRIMARY KEY, startTime bigint, endTime bigint, title text, users list<text>, description text);";
		this.cqlTemplate.execute(query5);
		
		String query6 = "create index if not exists email_index on stm_practice_awesome_name.schedule(startTime);";
		this.cqlTemplate.execute(query6);
		
		String query7 = "create index if not exists email_index on stm_practice_awesome_name.schedule(endTime);";
		this.cqlTemplate.execute(query7);
		
		String query8 = "DROP TABLE IF EXISTS stm_practice_awesome_name.scheduleUsers;";
		this.cqlTemplate.execute(query8);
		
		String query9 = "CREATE TABLE stm_practice_awesome_name.scheduleUsers(userid text, scheduleId text, startTime bigint, endTime bigint, title text, description text, Primary key(userid, startTime, endTime));";
		this.cqlTemplate.execute(query9);
	}
}
