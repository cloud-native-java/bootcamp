package com.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

// <4>
@RepositoryRestResource
interface CatRepository extends JpaRepository<Cat, Long> {
}

@SpringBootApplication
// <1>
public class DemoApplication {

 public static void main(String[] args) {
  SpringApplication.run(DemoApplication.class, args); // <2>
 }
}

// <3>
@Entity
class Cat {

 @Id
 @GeneratedValue
 private Long id;

 private String name;

 Cat() {
 }

 public Cat(String name) {
  this.name = name;
 }

 @Override
 public String toString() {
  return "Cat{" + "id=" + id + ", name='" + name + '\'' + '}';
 }

 public Long getId() {
  return id;
 }

 public String getName() {
  return name;
 }
}
