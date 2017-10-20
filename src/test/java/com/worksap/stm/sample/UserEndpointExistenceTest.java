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
public class UserEndpointExistenceTest {
	@Autowired
	private TestRestTemplate restTemplate;
	
	@Test
	public void testCreateUser() {
		ResponseEntity<String> r = this.restTemplate.postForEntity("/user", null, String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetSingleUser() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/user/RANDOMID", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
	
	@Test
	public void testGetAllUser() {
		ResponseEntity<String> r = this.restTemplate.getForEntity("/user", String.class);
		assertThat(r.getStatusCode()).isNotEqualTo(HttpStatus.NOT_FOUND);
	}
}
