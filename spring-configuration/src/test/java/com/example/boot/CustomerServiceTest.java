package com.example.boot;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.Matchers.containsString;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = ApplicationConfiguration.class)
@RunWith(SpringRunner.class)
public class CustomerServiceTest {

	@Rule
	public OutputCapture outputCapture = new OutputCapture();
	@Autowired
	private CustomerService customerService;

	@Test
	public void findAll() throws Exception {
		int size = this.customerService.findAll().size();
		Assert.assertEquals(size, 2);
		String consoleOutput = this.outputCapture.toString();
		Assert.assertThat(consoleOutput, containsString("starting @"));
		Assert.assertThat(consoleOutput, containsString("finishing @"));
	}
}