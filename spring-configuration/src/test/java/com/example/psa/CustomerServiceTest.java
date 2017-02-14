package com.example.psa;

import com.example.TestConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = { ApplicationConfiguration.class, TestConfiguration.class })
public class CustomerServiceTest {

	@Autowired
	private CustomerService customerService;

	@Test
	public void findAll() throws Exception {
		int size = this.customerService.findAll().size();
		Assert.assertEquals(size, 2);
	}
}