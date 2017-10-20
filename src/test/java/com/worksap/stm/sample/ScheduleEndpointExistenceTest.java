package com.worksap.stm.sample;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@Import(TestConfig.class)
public class ScheduleEndpointExistenceTest {
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void testCreateSchedule() {
		ResponseEntity<String> r = this.restTemplate.postForEntity("/schedule", null, String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetSingleSchedule() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/schedule/RANDOMID", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetAllSchedule() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/schedule", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetAllScheduleForUser() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/user/USERID/schedule", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetAllScheduleForUserInDay() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/user/USERID/schedule/day/DATE", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetAllScheduleForUserInWeek() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/user/USERID/schedule/week/DATE", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetAllScheduleForUserInMonth() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/user/USERID/schedule/month/DATE", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	

}
