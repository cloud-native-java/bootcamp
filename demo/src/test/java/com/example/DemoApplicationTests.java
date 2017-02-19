package com.example;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.stream.Stream;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// <1>
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class DemoApplicationTests {

 // <2>
 @Autowired
 private MockMvc mvc;

 // <3>
 @Autowired
 private CatRepository catRepository;

 // <4>
 @Before
 public void before() throws Exception {
  Stream.of("Felix", "Garfield", "Whiskers").forEach(
   n -> catRepository.save(new Cat(n)));
 }

 // <5>
 @Test
 public void catsReflectedInRead() throws Exception {
  MediaType halJson = MediaType
   .parseMediaType("application/hal+json;charset=UTF-8");
  this.mvc
   .perform(get("/cats"))
   .andExpect(status().isOk())
   .andExpect(content().contentType(halJson))
   .andExpect(
    mvcResult -> {
     String contentAsString = mvcResult.getResponse().getContentAsString();
     assertTrue(contentAsString.split("totalElements")[1].split(":")[1].trim()
      .split(",")[0].equals("3"));
    });
 }
}
