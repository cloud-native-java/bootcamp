package com.example.boot;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = ApplicationConfiguration.class)
@AutoConfigureMockMvc
@RunWith(SpringRunner.class)
public class CustomerRestControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void readAll() throws Exception {
		this.mockMvc
			.perform(MockMvcRequestBuilders.get("/customers"))
			.andExpect(jsonPath("$.*", hasSize(2)))
			.andExpect(status().isOk())
			.andExpect(
				content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
	}
}