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
//		String query1 = "DROP KEYSPACE IF EXISTS stm_practice_awesome_name;";
//		this.cqlTemplate.execute(query1);
//
//		String query2 = "CREATE KEYSPACE stm_practice_awesome_name WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };";
//		this.cqlTemplate.execute(query2);
	}
	
	private void createColumnFamilies() {
//		String query1 = "DROP TABLE IF EXISTS stm_practice_awesome_name.user;";
//		this.cqlTemplate.execute(query1);
//		
//		String query2 = "CREATE TABLE stm_practice_awesome_name.user(userId text PRIMARY KEY, email text, name text);";
//		this.cqlTemplate.execute(query2);
	}
}
